package justbucket.videolib.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import justbucket.videolib.state.Resource

open class BaseViewModel<Data> : ViewModel() {

    protected val liveData = MutableLiveData<Resource<Data>>()

    fun getData() = liveData
}