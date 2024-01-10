package eu.kairat.dev.atchelper.ui.init

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InitViewModel : ViewModel() {

    private val _airframeId = MutableLiveData<String>().apply {
        value = ""
    }
    val airframeId: LiveData<String> = _airframeId

    fun setAirframeId(id : String) {
        _airframeId.value = id
    }

    private val _callsign = MutableLiveData<String>().apply {
        value = ""
    }
    val callsign: LiveData<String> = _callsign

        /*
        get() {
            Log.d("XXX", "XXX READING CAllSIGN")
            return "FOO"
        }
        set(inValue) {
            Log.d("XXX", "XXX SETTING CAllSIGN")
            _callsign.value = inValue
        }
        */
/*
    fun getCallsign() : String {
        Log.d("XXX", "XXX READING CAllSIGN")
        if(callsign.value.isNullOrEmpty()) return ""
        return callsign.value!!
    }

    fun setCallsign(callsign : String) {
        Log.d("XXX", "XXX SETTING CAllSIGN")
        _callsign.value = callsign
    }
*/

    private val _instructor = MutableLiveData<String>().apply {
        value = ""
    }
    val instructor: LiveData<String> = _instructor

    fun setInstructor(instructor : String) {
        _instructor.value = instructor
    }

    private val _student = MutableLiveData<String>().apply {
        value = ""
    }
    val student: LiveData<String> = _student

    fun setStudent(student : String) {
        _student.value = student
    }

}