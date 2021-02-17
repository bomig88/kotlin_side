package bomi.kotlinside.ui.intro

import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bomi.kotlinside.R
import bomi.kotlinside.api.res.VisitorJejuVO
import bomi.kotlinside.base.api.ApiDataModel
import bomi.kotlinside.base.ui.viewmodel.BaseViewModel

class IntroViewModel(private val apiModule : ApiDataModel) : BaseViewModel() {

    private val _visitorJeju = MutableLiveData<VisitorJejuVO?>()
    val visitorJeju: LiveData<VisitorJejuVO?> = _visitorJeju
    fun getVisitorJeju(
        key:String,
        startDate:String,
        endDate:String,
        nationality:String
    ) {
        addDisposable(
            apiModule.getVisitorJeju(key, startDate, endDate, nationality),
            callbacks = {
                if(it.isSuccessful) {
                    _visitorJeju.value = it.body()
                } else
                    showAlert(
                        R.string.err_content_net_err_retry,
                        R.string.btn_retry,
                        R.string.btn_quit,
                        DialogInterface.OnClickListener { _, which ->
                            if(which == DialogInterface.BUTTON_POSITIVE) {
                                getVisitorJeju(key, startDate, endDate, nationality)
                            } else {
                                _quitApp.call()
                            }
                        })
            },
            throwable = {
                it.printStackTrace()
                showNetworkErr(it, null, true) {
                    getVisitorJeju(key, startDate, endDate, nationality)
                }
            },
            showLoading = false
        )
    }

}