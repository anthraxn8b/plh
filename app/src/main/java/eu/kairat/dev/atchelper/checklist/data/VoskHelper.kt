package eu.kairat.dev.atchelper.checklist.data

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONArray
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
            Log.e("VOSK HELPER", "Failed to unpack the model: " + exception.message)
        })
    }

    fun stopListening() {
        speechService?.stop()
        speechService = null
    }

    fun listen(
        matchOneOf: List<String>, noOfAllowedMisses: Int, callback: (String) -> Unit
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
                Log.d("SPEECH", "Unable to recognize valid voice input.")
                stopListening()
                return false
            }
            return true
        }

        stopListening()
        val ml = mutableListOf<String>()
            ml.addAll(matchOneOf)
            ml.add("[unk]")
        val rec = Recognizer(model, 16000.0f, JSONArray(ml).toString())
        //val rec = Recognizer(model, 16000.0f)
        speechService = SpeechService(rec, 16000.0f)
        speechService!!.startListening(object : RecognitionListener {

            override fun onPartialResult(hypothesis: String?) {
                Log.v("SPEECH", "onPartialResult()... Hypothesis: $hypothesis")
            }

            override fun onResult(hypothesis: String) {

                val recognized = JSONObject(hypothesis).get("text") as String

                if(recognized.isBlank()) {
                    Log.v("SPEECH", "onResult()...EMPTY")
                    retry()
                    return
                } else if(matchOneOf.contains(recognized)) {
                    Log.d("SPEECH", "onResult()... Hypothesis: $recognized")
                    callback(recognized)
                    stopListening()
                    return
                }

                // does not match
                Log.d("SPEECH", "onResult()... Unknown: $recognized")
                retry()
                return
            }

            override fun onFinalResult(hypothesis: String?) {
                Log.v("SPEECH", "onFinalResult()...")
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