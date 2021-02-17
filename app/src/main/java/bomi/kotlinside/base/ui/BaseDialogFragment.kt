package bomi.kotlinside.base.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import bomi.kotlinside.base.ui.dialog.DLoading
import bomi.kotlinside.base.ui.viewmodel.BaseViewModel
import bomi.kotlinside.util.PreferenceUtil
import org.koin.android.ext.android.inject

@Suppress("unused")
abstract class BaseDialogFragment<T : ViewDataBinding, R : BaseViewModel> : DialogFragment() {

    lateinit var viewDataBinding: T

    /**
     * setContentView로 호출할 Layout의 리소스 Id.
     * ex) R.layout.activity_sbs_main
     */
    abstract val layoutResourceId: Int

    /**
     * viewModel 로 쓰일 변수.
     */
    abstract val viewModel: R

    /**
     * Network working Progress Count
     * 0 = progress dialog is Hidden
     */
    private var nCntProgress: Int = 0
    private lateinit var dialogLoading: DLoading
    private lateinit var mContext: Context

    private var aeListener: OnActivityEnabledListener? = null
    protected val mgrPref: PreferenceUtil by inject()

    abstract fun onBackPressed() : Boolean

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        dialogLoading = DLoading(context)
        aeListener?.onActivityEnabled(context as BaseActivity<*, *>?)
    }

    fun setFullScreen() {
        dialog?.window?.let { window ->
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toastObserving()
        dialogAlertObserving()
        loadingObserving()
    }

    protected fun getAvailableActivity(listener: OnActivityEnabledListener) {
        activity?.let{
            listener.onActivityEnabled(activity as BaseActivity<*, *>?)
        } ?: setActivityEnabledListener(listener)
    }

    private fun setActivityEnabledListener(listener: OnActivityEnabledListener) {
        aeListener = listener
    }

//    protected fun setTitleListener(title: TitleBar) {
//        title.setOnClickMenuListener(object : OnClickTitleListener {
//            override fun onClick(isLeft: Boolean) {
//                back()
//            }
//        })
//    }

//    protected fun setTitleListener(title: SimpleTitleBar) {
//        title.setOnClickMenuListener(object : OnClickTitleListener {
//            override fun onClick(isLeft: Boolean) {
//                back()
//            }
//        })
//    }

    protected fun back() {
        back()
    }

    protected fun showToast(@StringRes msg:Int) {
        showToast(getString(msg))
    }

    protected fun showToast(msg:String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun toastObserving() {
        viewModel.addObserver(this, viewModel.toastMessage, Observer {
            showToast(it)
        })

        viewModel.addObserver(this, viewModel.toastMessageString, Observer {
            showToast(it)
        })
    }

    private fun dialogAlertObserving() {
        viewModel.addObserver(this, viewModel.dialogAlertMessage, Observer {
            it.convertAlertDialogBuilder(mContext)
                .show()
        })
        viewModel.addObserver(this, viewModel.dialogAlertMessageString, Observer {
            it.convertAlertDialogBuilder(mContext)
                .show()
        })
    }

    private fun loadingObserving() {
        viewModel.addObserver(this, viewModel.loadingChange, Observer { bShow ->
            if (bShow) {
                if (nCntProgress == 0) {
                    if (!dialogLoading.isShowing)
                        dialogLoading.show()
                }

                nCntProgress++
            } else {
                if (nCntProgress == 1) {
                    dialogLoading.dismiss()
                }

                nCntProgress--
            }
        })

        viewModel.addObserver(this, viewModel.loadingClear, Observer {
            dialogLoading.dismiss()
            nCntProgress = 0
        })
    }

    var runKeyboardHide = Runnable {
        val activity = activity
        if (activity != null) {
            val focusView = activity.currentFocus
            focusView?.clearFocus()
        }
    }

    override fun onDetach() {
        super.onDetach()
        viewModel.clearLoading()
    }
}