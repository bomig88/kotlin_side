package bomi.kotlinside.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bomi.kotlinside.api.ApiDataModel
import com.google.gson.JsonObject

open class BaseApiViewModel(private val apiModule : ApiDataModel) : BaseViewModel() {
    private val _btnEnabled = MutableLiveData(false)
    val btnEnabled : LiveData<Boolean> = _btnEnabled
    fun setBtnEnabled(enabled:Boolean) {
        _btnEnabled.value = enabled
    }

    private val _clickNext = SingleLiveEvent<Any>()
    val clickNext : LiveData<Any> = _clickNext
    fun clickNext() {
        _clickNext.call()
    }

    private val _clickBack = SingleLiveEvent<Any>()
    val clickBack : LiveData<Any> = _clickBack
    fun clickBack() {
        _clickBack.call()
    }

    private val _clickExit = SingleLiveEvent<Any>()
    val clickExit : LiveData<Any> = _clickExit
    fun clickExit() {
        _clickExit.call()
    }

    ////////// API ////////////////
}