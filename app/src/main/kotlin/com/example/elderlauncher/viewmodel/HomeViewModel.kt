package com.example.elderlauncher.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.elderlauncher.CallMode
import com.example.elderlauncher.ElderLauncherApp
import com.example.elderlauncher.R
import com.example.elderlauncher.WeChatAccessibilityService
import com.example.elderlauncher.WeChatHelper
import com.example.elderlauncher.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as ElderLauncherApp).repository

    val contacts: StateFlow<List<Contact>> = repo.observeContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _callingState = MutableStateFlow<CallingState>(CallingState.Idle)
    val callingState: StateFlow<CallingState> = _callingState.asStateFlow()

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private val _voicePromptEnabled = MutableStateFlow(
        application.getSharedPreferences("settings", 0).getBoolean("voice_prompt", true)
    )
    val voicePromptEnabled: StateFlow<Boolean> = _voicePromptEnabled.asStateFlow()

    init {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.CHINESE
                ttsReady = true
            }
        }
    }

    fun initiateCall(contact: Contact, mode: CallMode) {
        val context = getApplication<ElderLauncherApp>()

        if (!WeChatHelper.isWeChatInstalled(context)) {
            Toast.makeText(context, R.string.wechat_not_installed, Toast.LENGTH_LONG).show()
            return
        }

        if (!WeChatHelper.isAccessibilityServiceEnabled(context)) {
            Toast.makeText(context, R.string.accessibility_not_enabled, Toast.LENGTH_LONG).show()
            return
        }

        val prompt = when (mode) {
            CallMode.VOICE -> context.getString(R.string.calling_voice, contact.name)
            CallMode.VIDEO -> context.getString(R.string.calling_video, contact.name)
        }

        if (_voicePromptEnabled.value && ttsReady) {
            tts?.speak(prompt, TextToSpeech.QUEUE_FLUSH, null, "call_prompt")
        }

        _callingState.value = CallingState.Calling(contact.name, mode)

        WeChatAccessibilityService.startCall(contact.wechatId, mode)

        // Ensure we have a fallback in case service unavailable/died - clean up gracefully
        viewModelScope.launch {
            kotlinx.coroutines.delay(5000)
            _callingState.value = CallingState.Idle
            // Attempt graceful stop after timeout to release any pending automation
            WeChatAccessibilityService.stopCall()
        }

        // Register termination callback for graceful cleanup
        WeChatAccessibilityService.setOnTerminatedCallback {
            _callingState.value = CallingState.Idle
        }
    }

    override fun onCleared() {
        tts?.shutdown()
        super.onCleared()
    }

    sealed class CallingState {
        object Idle : CallingState()
        data class Calling(val contactName: String, val mode: CallMode) : CallingState()
    }
}
