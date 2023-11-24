package eu.kairat.dev.atchelper.checklist.data

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONObject
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import java.io.IOException

class VoskHelper(private val activity: Activity) {

    private lateinit var model: Model
    private var speechService: SpeechService? = null

    private var activated = true

    init {
        LibVosk.setLogLevel(LogLevel.INFO)

        // Check if user has given permission to record audio, init the model after permission is granted
        val permissionCheck = ContextCompat.checkSelfPermission(
            activity.applicationContext, Manifest.permission.RECORD_AUDIO
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.RECORD_AUDIO), 400
            )
        }
        initModel()
    }

    private fun initModel() {
        StorageService.unpack(activity, "model-en-us", "model", { model: Model? ->
            this.model = model!!
            Log.d("VOSK HELPER", "Model loaded.")
        }, { exception: IOException ->
            //setErrorState(
            Log.e("VOSK HELPER", "Failed to unpack the model: " + exception.message)
            //)
        })
    }

    fun stopListening() {
        speechService?.stop()
        speechService = null
    }

    fun listen(
        matchOneOf: List<String>, noOfAllowedMisses: Int, callback: () -> Unit
    ) {

        if (!activated) {
            Log.d("SPEECH", "STT is disabled.")
            stopListening()
            return
        }

        var retryCount = 0
        fun retry(): Boolean {
            retryCount++
            if (noOfAllowedMisses < retryCount) {
                Log.d("SPEECH", "You retried too often.")
                stopListening()
                return false
            }
            return true
        }

        val rec = Recognizer(model, 16000.0f)
        speechService = SpeechService(rec, 16000.0f)
        speechService!!.startListening(object : RecognitionListener {

            override fun onPartialResult(hypothesis: String?) {
                //Log.d("SPEECH", "onPartialResult()... Hypothesis: $hypothesis")
            }

            override fun onResult(hypothesis: String?) {
                if (!hypothesis.isNullOrEmpty()) {
                    val jsonObject = JSONObject(hypothesis)
                    if (jsonObject.has("text")) {
                        val text = (jsonObject.get("text") as String)
                        Log.d("SPEECH", "onResult()... Hypothesis: $text")
                        if (matchOneOf.contains(text)) {
                            Log.d("SPEECH", "onResult()... Hit. Callback.")
                            callback()
                            stopListening()
                            return
                        }
                    }
                }
                Log.d("SPEECH", "onResult()...")
                retry()
            }

            override fun onFinalResult(hypothesis: String?) {
                Log.d("SPEECH", "onFinalResult()...")
            }

            override fun onError(exception: Exception?) {
                Log.d("SPEECH", "onError()... Exception: $exception")
                retry()
            }

            override fun onTimeout() {
                Log.d("SPEECH", "onTimeout()...")
                retry()
            }

        })
    }

    fun toggleOnOff() {
        activated = !activated
        if (!activated) {
            stopListening()
        }
    }

    fun onDestroy() {
        speechService?.stop()
        speechService?.shutdown()
    }

}