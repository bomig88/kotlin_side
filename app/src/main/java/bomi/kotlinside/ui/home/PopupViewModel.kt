package bomi.kotlinside.ui.home

import androidx.lifecycle.LiveData
import bomi.kotlinside.base.ui.viewmodel.BaseViewModel
import bomi.kotlinside.base.ui.viewmodel.SingleLiveEvent

class PopupViewModel : BaseViewModel() {

    private val _clickClose =
        SingleLiveEvent<Any>()
    val clickClose : LiveData<Any> = _clickClose
    fun clickClose() {
        _clickClose.call()
    }

}