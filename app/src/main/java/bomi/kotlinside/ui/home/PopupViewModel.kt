package bomi.kotlinside.ui.home

import androidx.lifecycle.LiveData
import bomi.kotlinside.ui.base.BaseViewModel
import bomi.kotlinside.ui.base.SingleLiveEvent

class PopupViewModel : BaseViewModel() {

    private val _clickClose = SingleLiveEvent<Any>()
    val clickClose : LiveData<Any> = _clickClose
    fun clickClose() {
        _clickClose.call()
    }

}