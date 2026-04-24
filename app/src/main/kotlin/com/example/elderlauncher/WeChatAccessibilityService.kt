package com.example.elderlauncher

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log

enum class CallMode { VOICE, VIDEO }

class WeChatAccessibilityService : AccessibilityService() {
    companion object {
        @Volatile
        var instance: WeChatAccessibilityService? = null
        fun triggerCall(contactName: String, mode: CallMode) {
            instance?.performCall(contactName, mode)
        }
    }
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i("WeChatAS", "Service connected")
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Placeholder for automation events
    }
    override fun onInterrupt() {
        instance = null
        Log.i("WeChatAS", "Service interrupted")
    }
    fun performCall(contactName: String, mode: CallMode) {
        Log.i("WeChatAS", "Performing ${mode.name} call to $contactName (skeleton)")
        // Real UI automation would go here
    }
}
