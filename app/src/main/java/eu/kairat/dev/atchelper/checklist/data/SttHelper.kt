package eu.kairat.dev.atchelper.checklist.data

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.Locale

// TODO: READ: https://www.tensorflow.org/lite/android/tutorials/audio_classification
class SttHelper(activity: Activity, x: Boolean) {

    private var speechRecognizerIntent: Intent
    private var speechRecognizer: SpeechRecognizer

    private var activated = true

    init {
        if (activity.applicationContext.checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                300
            )
        }
        if (activity.applicationContext.checkSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.INTERNET),
                301
            )
        }

        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        //speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
        //speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity.applicationContext)
    }

    fun listenx(callback: (String) -> Unit, retryCount: Int = 0) {

        if(!activated) {
            Log.d("SPEECH", "STT is disabled.")
            return
        }

        if( 3 < retryCount) {
            Log.d("SPEECH", "You retried too often.")
            return
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SPEECH", "onReadyForSpeech()...")
            }

            override fun onBeginningOfSpeech() {
                Log.d("SPEECH", "onBeginningOfSpeech()...")
                // TODO: Somehow show user that phone listens...
            }

            override fun onRmsChanged(rmsdB: Float) {
                //Log.d("SPEECH", "onRmsChanged()... to: $rmsdB")
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("SPEECH", "onBufferReceived()...")
            }

            override fun onEndOfSpeech() {
                Log.d("SPEECH", "onEndOfSpeech()...")
            }

            override fun onError(error: Int) {
                Log.d("SPEECH", "onError()... $error")
                if(7 == error) {
                    // retry X times
                    listenx(callback, retryCount + 1)
                }
            }

            override fun onResults(results: Bundle?) {
                Log.d("SPEECH", "onResults()...")

                val data: ArrayList<String>? =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                data?.forEach { Log.d("SPEECH", "Said: $it") }

                if(null == data || data.size < 1) {
                    // retry X times
                    listenx(callback, retryCount + 1)
                } else {
                    callback(data[0])
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d("SPEECH", "onPartialResults()... ${partialResults.toString()}")
/*
                val data         = partialResults!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val unstableData = partialResults!!.getStringArrayList("android.speech.extra.UNSTABLE_TEXT")
                val mResult = data!![0] + unstableData!![0]
                Log.d("SPEECH", "Partial: $mResult")
*/
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("SPEECH", "onEvent()...")
            }

        })

        speechRecognizer.startListening(speechRecognizerIntent)

    }

    fun stopListeningx() {
        speechRecognizer.stopListening()
    }

    fun toggleOnOffx() {
        activated = !activated
        if(!activated) {
            stopListeningx()
        }
    }

    fun destroyx() {

    }
}