package bomi.kotlinside.ui.base

import android.content.DialogInterface
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import bomi.kotlinside.R
import bomi.kotlinside.exception.NoConnectivityException
import bomi.kotlinside.ui.dialog.DAlertParam
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

@Suppress("unused", "PropertyName", "MemberVisibilityCanBePrivate")
open class BaseViewModel : ViewModel() {
    // 일회성 이벤트를 만들어 내는 라이브 이벤트
    // 뷰는 이러한 이벤트를 바인딩하고 있다가, 적절한 상황이 되면 액티비티를 호출하거나 스낵바를 만듬
    val toastMessage = ToastMessage()
    val toastMessageString = ToastMessageString()

    val dialogAlertMessage = DialogAlertMessage()
    val dialogAlertMessageString = DialogAlertMessageString()

    val loadingChange = LoadingChange()
    val loadingClear = LoadingClear()

    protected val _quitApp = SingleLiveEvent<Any>()
    val quitApp: LiveData<Any> get() = _quitApp

    private val _callNetworkSetting = SingleLiveEvent<Any>()
    val callNetworkSetting: LiveData<Any> get() = _callNetworkSetting

    /**
     * RxJava 의 observing을 위한 부분.
     * addDisposable을 이용하여 추가하기만 하면 된다
     */
    private val compositeDisposable = CompositeDisposable()

    fun <T> addDisposable(
        parent: Single<T>,
        callbacks: (T) -> Unit,
        throwable: (Throwable) -> Unit,
        showLoading: Boolean
    ) {
        if (showLoading) changeLoading(bShow = true)
        compositeDisposable.add(parent
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (showLoading) changeLoading(bShow = false)
                callbacks(it)
            }, {
                if (showLoading) changeLoading(bShow = false)
                throwable(it)
            })
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun showNetworkErrMsg(
        resultMsg: String? = null,
        isQuit: Boolean,
        retryFunction: (() -> Unit)?
    ) {
        showNetworkErr(null, resultMsg, isQuit, retryFunction)
    }

    protected fun showNetworkErrThrowable(
        e: Throwable? = null,
        isQuit: Boolean,
        retryFunction: (() -> Unit)?
    ) {
        showNetworkErr(e, null, isQuit, retryFunction)
    }

    private var _lastRetryFunction: (() -> Unit)? = null
    val lastRetryFunction get() = _lastRetryFunction

    protected fun showNetworkErr(
        e: Throwable? = null,
        resultMsg: String? = null,
        isQuit: Boolean,
        retryFunction: (() -> Unit)?
    ) {
        val negativeButtonName = if (isQuit) R.string.btn_quit else R.string.btn_cancel
        when (e) {
            is NoConnectivityException -> {
                _lastRetryFunction = retryFunction
                showAlert(
                    R.string.err_content_network_off,
                    R.string.btn_net_setting,
                    negativeButtonName,
                    DialogInterface.OnClickListener { _, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                _callNetworkSetting.call()
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                if (isQuit) _quitApp.call()
                            }
                        }
                    })
            }
            else -> {
                _lastRetryFunction = null
                if (resultMsg?.isNotEmpty() == true) {
                    showAlert(
                        resultMsg,
                        R.string.btn_retry,
                        negativeButtonName,
                        DialogInterface.OnClickListener { _, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    retryFunction?.invoke()
                                }
                                DialogInterface.BUTTON_NEGATIVE -> {
                                    if (isQuit) _quitApp.call()
                                }
                            }
                        })
                } else {
                    showAlert(
                        R.string.err_content_net_err_retry,
                        R.string.btn_retry,
                        negativeButtonName,
                        DialogInterface.OnClickListener { _, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    retryFunction?.invoke()
                                }
                                DialogInterface.BUTTON_NEGATIVE -> {
                                    if (isQuit) _quitApp.call()
                                }
                            }
                        })
                }
            }
        }
    }

    protected fun showNetworkErr(
        e: Throwable? = null,
        resultMsg: String? = null,
        retryFunction: (() -> Unit)?,
        closeFunction: (() -> Unit)?
    ) {
        when (e) {
            is NoConnectivityException -> {
                _lastRetryFunction = retryFunction
                showAlert(
                    R.string.err_content_network_off,
                    R.string.btn_net_setting,
                    R.string.btn_cancel,
                    DialogInterface.OnClickListener { _, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                _callNetworkSetting.call()
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                                closeFunction?.invoke()
                            }
                        }
                    })
            }
            else -> {
                _lastRetryFunction = null
                if (resultMsg?.isNotEmpty() == true) {
                    showAlert(
                        resultMsg,
                        R.string.btn_retry,
                        R.string.btn_cancel,
                        DialogInterface.OnClickListener { _, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    retryFunction?.invoke()
                                }
                                DialogInterface.BUTTON_NEGATIVE -> {
                                    closeFunction?.invoke()
                                }
                            }
                        })
                } else {
                    showAlert(
                        R.string.err_content_net_err_retry,
                        R.string.btn_retry,
                        R.string.btn_cancel,
                        DialogInterface.OnClickListener { _, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    retryFunction?.invoke()
                                }
                                DialogInterface.BUTTON_NEGATIVE -> {
                                    closeFunction?.invoke()
                                }
                            }
                        })
                }
            }
        }
    }

    /**
     * 스낵바를 보여주고 싶으면 viewModel 에서 이 함수를 호출
     */
    fun showToast(stringResourceId: Int) {
        toastMessage.value = stringResourceId
    }

    fun showToast(str: String) {
        toastMessageString.value = str
    }

    fun showAlert(
        stringResourceId: Int,
        positiveButtonResourceId: Int = R.string.btn_confirm,
        negativeButtonResourceId: Int = R.string.btn_cancel,
        lsnDialogButton: DialogInterface.OnClickListener? = null
    ) {

        dialogAlertMessage.value = DAlertParam.Builder()
            .setMessage(stringResourceId)
            .setPositiveButton(positiveButtonResourceId, lsnDialogButton)
            .setNegativeButton(negativeButtonResourceId, lsnDialogButton)
            .create()
    }

    fun showAlert(
        str: String,
        positiveButtonResourceId: Int = R.string.btn_confirm,
        negativeButtonResourceId: Int = R.string.btn_cancel,
        lsnDialogButton: DialogInterface.OnClickListener? = null
    ) {
        dialogAlertMessage.value = DAlertParam.Builder()
            .setMessage(str)
            .setPositiveButton(positiveButtonResourceId, lsnDialogButton)
            .setNegativeButton(negativeButtonResourceId, lsnDialogButton)
            .create()
    }

    fun showAlertConfirm(
        stringResourceId: Int,
        positiveButtonResourceId: Int = R.string.btn_confirm,
        lsnDialogButton: DialogInterface.OnClickListener? = null
    ) {
        dialogAlertMessage.value = DAlertParam.Builder()
            .setMessage(stringResourceId)
            .setPositiveButton(positiveButtonResourceId, lsnDialogButton)
            .create()
    }

    fun showAlertConfirm(
        str: String,
        positiveButtonResourceId: Int = R.string.btn_confirm,
        lsnDialogButton: DialogInterface.OnClickListener? = null
    ) {
        dialogAlertMessage.value = DAlertParam.Builder()
            .setMessage(str)
            .setPositiveButton(positiveButtonResourceId, lsnDialogButton)
            .create()
    }

    private fun changeLoading(bShow: Boolean) {
        loadingChange.value = bShow
    }

    fun clearLoading() {
        loadingClear.value = Random().nextInt()
    }

    fun <T> addObserver(
        owner: LifecycleOwner,
        param: LiveData<T>,
        observer: androidx.lifecycle.Observer<T>
    ) {
        if (!param.hasObservers())
            param.observe(owner, observer)
    }
}