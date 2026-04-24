package com.example.elderlauncher

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

enum class CallMode { VOICE, VIDEO }

class WeChatAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "WeChatAS"
        private const val WECHAT_PACKAGE = "com.tencent.mm"

        @Volatile
        var instance: WeChatAccessibilityService? = null
            private set

        fun isRunning(): Boolean = instance != null

        fun startCall(contactWeChatId: String, mode: CallMode) {
            val svc = instance ?: run {
                Log.w(TAG, "Service not running")
                return
            }
            svc.beginAutomation(contactWeChatId, mode)
        }
        // Expose a safe stop path for external triggers (e.g., on user action or app lifecycle)
        fun stopCall() {
            instance?.stopAutomation()
        }

        // Callback for termination notifications (called when automation ends or errors)
        private var onTerminated: (() -> Unit)? = null
        fun setOnTerminatedCallback(callback: () -> Unit) {
            onTerminated = callback
        }

        // Internal method to invoke termination callback (call from service instance)
        internal fun signalTerminated() {
            onTerminated?.invoke()
        }
    }

    private enum class AutoState {
        IDLE,
        WAITING_MAIN,
        WAITING_SEARCH,
        TYPING_SEARCH,
        WAITING_RESULT,
        WAITING_CHAT,
        CLICKING_MORE,
        SELECTING_CALL
    }

    private var state = AutoState.IDLE
    private var targetContact = ""
    private var callMode = CallMode.VOICE
    private val handler = Handler(Looper.getMainLooper())
    private var retryCount = 0
    private val maxRetries = 15

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i(TAG, "Accessibility service connected")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        Log.i(TAG, "Accessibility service destroyed")
    }

    override fun onInterrupt() {
        state = AutoState.IDLE
        instance = null
    }

    private fun beginAutomation(contactId: String, mode: CallMode) {
        targetContact = contactId
        callMode = mode
        retryCount = 0
        state = AutoState.WAITING_MAIN

        if (!WeChatHelper.launchWeChat(this)) {
            Log.e(TAG, "Failed to launch WeChat")
            state = AutoState.IDLE
            return
        }

        handler.postDelayed({ processCurrentState() }, 1500)
    }

    // Public API to stop ongoing automation gracefully
    fun stopAutomation() {
        state = AutoState.IDLE
        targetContact = ""
        retryCount = 0
        handler.removeCallbacksAndMessages(null)
        Log.i(TAG, "Automation stopped by user/request")
        // Notify external consumer (ViewModel) about termination
        signalTerminated()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || state == AutoState.IDLE) return
        if (event.packageName?.toString() != WECHAT_PACKAGE) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({ processCurrentState() }, 500)
            }
        }
    }

    private fun processCurrentState() {
        if (state == AutoState.IDLE) return

        retryCount++
        if (retryCount > maxRetries) {
            Log.w(TAG, "Max retries reached at state $state, aborting")
            state = AutoState.IDLE
            return
        }

        val root = rootInActiveWindow ?: run {
            scheduleRetry()
            return
        }

        when (state) {
            AutoState.WAITING_MAIN -> handleWaitingMain(root)
            AutoState.WAITING_SEARCH -> handleWaitingSearch(root)
            AutoState.TYPING_SEARCH -> handleTypingSearch(root)
            AutoState.WAITING_RESULT -> handleWaitingResult(root)
            AutoState.WAITING_CHAT -> handleWaitingChat(root)
            AutoState.CLICKING_MORE -> handleClickingMore(root)
            AutoState.SELECTING_CALL -> handleSelectingCall(root)
            AutoState.IDLE -> {}
        }
        root.recycle()
    }

    private fun handleWaitingMain(root: AccessibilityNodeInfo) {
        val searchBtn = findNodeByText(root, "搜索")
            ?: findNodeByContentDescription(root, "搜索")

        if (searchBtn != null) {
            clickNode(searchBtn)
            state = AutoState.WAITING_SEARCH
            retryCount = 0
        } else {
            scheduleRetry()
        }
    }

    private fun handleWaitingSearch(root: AccessibilityNodeInfo) {
        val editText = findNodeByClassName(root, "android.widget.EditText")
        if (editText != null) {
            state = AutoState.TYPING_SEARCH
            retryCount = 0
            handleTypingSearch(root)
        } else {
            scheduleRetry()
        }
    }

    private fun handleTypingSearch(root: AccessibilityNodeInfo) {
        val editText = findNodeByClassName(root, "android.widget.EditText")
        if (editText != null) {
            val args = Bundle().apply {
                putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    targetContact
                )
            }
            editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
            state = AutoState.WAITING_RESULT
            retryCount = 0
            handler.postDelayed({ processCurrentState() }, 1000)
        } else {
            scheduleRetry()
        }
    }

    private fun handleWaitingResult(root: AccessibilityNodeInfo) {
        val contactNode = findNodeByText(root, targetContact)
        if (contactNode != null) {
            val clickable = findClickableParent(contactNode)
            if (clickable != null) {
                clickNode(clickable)
                state = AutoState.WAITING_CHAT
                retryCount = 0
            } else {
                clickNode(contactNode)
                state = AutoState.WAITING_CHAT
                retryCount = 0
            }
        } else {
            scheduleRetry()
        }
    }

    private fun handleWaitingChat(root: AccessibilityNodeInfo) {
        val moreBtn = findNodeByContentDescription(root, "更多功能按钮")
            ?: findNodeByText(root, "+")
            ?: findNodeByContentDescription(root, "添加")

        if (moreBtn != null) {
            clickNode(moreBtn)
            state = AutoState.CLICKING_MORE
            retryCount = 0
        } else {
            scheduleRetry()
        }
    }

    private fun handleClickingMore(root: AccessibilityNodeInfo) {
        state = AutoState.SELECTING_CALL
        retryCount = 0
        handler.postDelayed({ processCurrentState() }, 800)
    }

    private fun handleSelectingCall(root: AccessibilityNodeInfo) {
        val targetText = when (callMode) {
            CallMode.VOICE -> "语音通话"
            CallMode.VIDEO -> "视频通话"
        }

        val callBtn = findNodeByText(root, targetText)
        if (callBtn != null) {
            val clickable = findClickableParent(callBtn) ?: callBtn
            clickNode(clickable)
            state = AutoState.IDLE
            retryCount = 0
            Log.i(TAG, "Successfully initiated $targetText with $targetContact")
        } else {
            scheduleRetry()
        }
    }

    private fun scheduleRetry() {
        handler.postDelayed({ processCurrentState() }, 800)
    }

    private fun findNodeByText(root: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
        val nodes = root.findAccessibilityNodeInfosByText(text)
        return nodes?.firstOrNull()
    }

    private fun findNodeByContentDescription(
        root: AccessibilityNodeInfo,
        desc: String
    ): AccessibilityNodeInfo? {
        for (i in 0 until root.childCount) {
            val child = root.getChild(i) ?: continue
            if (child.contentDescription?.toString()?.contains(desc) == true) {
                return child
            }
            val found = findNodeByContentDescription(child, desc)
            if (found != null) return found
            child.recycle()
        }
        return null
    }

    private fun findNodeByClassName(
        root: AccessibilityNodeInfo,
        className: String
    ): AccessibilityNodeInfo? {
        if (root.className?.toString() == className) return root
        for (i in 0 until root.childCount) {
            val child = root.getChild(i) ?: continue
            val found = findNodeByClassName(child, className)
            if (found != null) return found
            child.recycle()
        }
        return null
    }

    private fun findClickableParent(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        var current = node.parent
        var depth = 0
        while (current != null && depth < 5) {
            if (current.isClickable) return current
            current = current.parent
            depth++
        }
        return null
    }

    private fun clickNode(node: AccessibilityNodeInfo) {
        if (node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        } else {
            val parent = findClickableParent(node)
            parent?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }
}
