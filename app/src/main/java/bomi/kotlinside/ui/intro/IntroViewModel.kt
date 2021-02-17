package bomi.kotlinside.ui.intro

import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bomi.kotlinside.R
import bomi.kotlinside.base.api.ApiDataModel
import bomi.kotlinside.api.res.ResPopupVO
import bomi.kotlinside.base.ui.viewmodel.BaseViewModel

class IntroViewModel(private val apiModule : ApiDataModel) : BaseViewModel() {

    //팝업 정보 확인
    private val _popupVO = MutableLiveData<ResPopupVO>()
    val popupVO: LiveData<ResPopupVO> = _popupVO
    fun getPopupVO(serviceType:String) {
        addDisposable(
            apiModule.getPopupList(
                serviceType
            )
            , callbacks = {
                if(it.resCodeIsSuccess) {
                    _popupVO.value = it.data

                } else {
                    showAlert(
                        R.string.err_content_net_err_retry,
                        R.string.btn_retry,
                        R.string.btn_quit,
                        DialogInterface.OnClickListener { _, which ->
                            if(which == DialogInterface.BUTTON_POSITIVE) {
                                getPopupVO(serviceType)
                            } else {
                                _quitApp.call()
                            }
                        })
                }
            }
            , throwable =  {
                it.printStackTrace()
                showNetworkErr(it, null, true) {
                    getPopupVO(serviceType)
                }
            }
            , showLoading = false
        )
    }
}