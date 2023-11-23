package eu.kairat.dev.atchelper.ui.init

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
}