package bomi.kotlinside.ui.intro

import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bomi.kotlinside.R
import bomi.kotlinside.base.api.ApiDataModel
import bomi.kotlinside.base.ui.viewmodel.BaseViewModel

class IntroViewModel(private val apiModule : ApiDataModel) : BaseViewModel() {

    private val _xml = MutableLiveData<String>()
    val xml: LiveData<String> = _xml
    fun getXml() {
        addDisposable(
            apiModule.getXml(),
            callbacks = {
                if(it.isSuccessful)
                    _xml.value = it.body()?.string() ?:""
                else
                    showAlert(
                        R.string.err_content_net_err_retry,
                        R.string.btn_retry,
                        R.string.btn_quit,
                        DialogInterface.OnClickListener { _, which ->
                            if(which == DialogInterface.BUTTON_POSITIVE) {
                                getXml()
                            } else {
                                _quitApp.call()
                            }
                        })
            },
            throwable = {
                it.printStackTrace()
                showNetworkErr(it, null, true) {
                    getXml()
                }
            },
            showLoading = false
        )
    }

}