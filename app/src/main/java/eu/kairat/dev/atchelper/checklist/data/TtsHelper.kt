package eu.kairat.dev.atchelper.checklist.data

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import eu.kairat.dev.atchelper.checklist.data.structure.AirframeData
import eu.kairat.dev.atchelper.ui.checklists.ChecklistLogic
import java.util.Locale
import kotlin.random.Random

class TtsHelper(activity: Activity) {

    private lateinit var tts: TextToSpeech

    init {
        val toast = Toast.makeText(activity, "Initializing TTS...", Toast.LENGTH_SHORT)
        toast.show()

        tts = TextToSpeech(
            activity.applicationContext
        ) { status: Int ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.ENGLISH
                tts.speak("Text to speech initialized!", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    fun readPositionThenExecute(checklistItem: AirframeData.ChecklistItem, callback: () -> Unit) {
        //val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        //val ranStr = (1..5).map { Random.nextInt(0, charPool.size).let { charPool[it] } }.joinToString("")
        //readPosition(checklistItem, ranStr, object : UtteranceProgressListener() {
        readPosition(checklistItem, null, object : UtteranceProgressListener() {

            override fun onStart(utteranceId: String?) {
                Handler(Looper.getMainLooper()).post{ callback() }
            }

            override fun onDone(utteranceId: String?) {}

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {}

        })
        Handler(Looper.getMainLooper()).post{ callback() }
    }

    fun readChecklistComplete(
        checklist: AirframeData.Checklist,
        checklistItemSection: AirframeData.ChecklistSection?
    ) {
        if(null == checklistItemSection) {
            tts.speak("Checklist ${checklist.name} completed.", TextToSpeech.QUEUE_ADD, null, null)
        } else {
            tts.speak("Section ${checklistItemSection.name} completed.", TextToSpeech.QUEUE_ADD, null, null)
        }
    }

    fun readPositionWithAtc(checklistItem: AirframeData.ChecklistItem) {
        readPosition(checklistItem)
    }

    private fun readPosition(
        checklistItem: AirframeData.ChecklistItem,
        utteranceId: String? = null,
        utteranceProgressListener: UtteranceProgressListener? = null) {

        val description =
            if (!checklistItem.audioDescription.isNullOrEmpty()) checklistItem.audioDescription else checklistItem.visibleDescription

        var task = "check"
        if (!checklistItem.audioTask.isNullOrEmpty()) {
            task = checklistItem.audioTask
        } else if (!checklistItem.visibleTask.isNullOrEmpty()) {
            task = checklistItem.visibleTask
        }
        if(null != utteranceProgressListener) {
            tts.setOnUtteranceProgressListener(utteranceProgressListener)
        }
        tts.speak(description, TextToSpeech.QUEUE_ADD, null, null)
        tts.speak(task, TextToSpeech.QUEUE_ADD, null, utteranceId)

    }

    fun confirmPosition(audioConfirmation: String) {
        tts.speak(audioConfirmation, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun destroy() {
        tts.stop()
        tts.shutdown()
    }

    fun readNewChecklist(data: ChecklistLogic.FullSectionPath) {
        tts.speak("Next checklist: ${data.checklist.name}", TextToSpeech.QUEUE_ADD, null, null)
        if(data.section.name.isNotBlank()) {
            tts.speak("Next section: ${data.section.name}", TextToSpeech.QUEUE_ADD, null, null)
        }
    }
}
