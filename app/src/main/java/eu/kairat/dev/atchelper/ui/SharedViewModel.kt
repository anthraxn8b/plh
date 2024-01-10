package eu.kairat.dev.atchelper.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

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

    fun setCallsign(callsign : String) {
        _callsign.value = callsign
    }

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