package bomi.kotlinside.base.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import bomi.kotlinside.GlobalDefine
import bomi.kotlinside.base.ui.dialog.DAlertParam
import bomi.kotlinside.base.ui.dialog.DLoading
import bomi.kotlinside.base.ui.viewmodel.ActivityViewModel
import bomi.kotlinside.base.ui.viewmodel.BaseViewModel
import bomi.kotlinside.util.PreferenceUtil
import org.koin.android.ext.android.inject
import bomi.kotlinside.ui.home.HomeFragment
import bomi.kotlinside.ui.home.HomeTutorialFragment

@Suppress("unused")
abstract class BaseFragment<T : ViewDataBinding, R : BaseViewModel> : Fragment() {

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
    protected val activityViewModel: ActivityViewModel by activityViewModels()

    /**
     * Network working Progress Count
     * 0 = progress dialog is Hidden
     */
    private var nCntProgress: Int = 0
    private lateinit var dialogLoading: DLoading

    private var aeListener: OnActivityEnabledListener? = null
    protected val mgrPref: PreferenceUtil by inject()
    protected lateinit var mContext : Context
    private var lsnTransitAnimation: Animation.AnimationListener? = null

    companion object {
        const val REQUEST_ID = "REQUEST_ID"
        /**
         * 프레그먼트 처리 결과 코드 - 정상
         */
        val RESULT_OK = Activity.RESULT_OK
        /**
         * 프레그먼트 처리 결과 코드 - 기본값 / 처리 결과 보내지않음
         */
        val RESULT_CANCEL = Activity.RESULT_CANCELED
        val REQUEST_EMPTY = -99
    }

    protected var nRequestCode =
        REQUEST_EMPTY

    private var isFragmentAddType = false
    private var isFragmentAddHistory = true

    private var bundleResult: Bundle? = null
    private var nResultCode = 0

    abstract fun onBackPressed() : Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val arguments = arguments
        if (arguments != null && arguments.containsKey(REQUEST_ID))
            nRequestCode = arguments.getInt(REQUEST_ID)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        dialogLoading = DLoading(context)

        aeListener?.onActivityEnabled(context as BaseActivity<*, *>?)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutResourceId, container, false)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toastObserving()
        dialogAlertObserving()
        loadingObserving()
        callNetworkSettingObserving()
        quitAppObserving()

        //Touch 가 백그라운드 플래그먼트에 가지 않도록 방지
        if (isFragmentAddType()) {
            view.setOnClickListener { }
        }

    }

    protected fun getAvailableActivity(listener: OnActivityEnabledListener) {
        if (activity == null) {
            aeListener = listener
        } else {
            listener.onActivityEnabled(activity as BaseActivity<*, *>?)
        }
    }

    protected fun quitApp() {
        getAvailableActivity(object:
            OnActivityEnabledListener {
            override fun onActivityEnabled(activity: BaseActivity<*, *>?) {
                activity?.finishAffinity() ?:quitApp()
            }

        })
    }

    private fun callNetworkSettingObserving() {
        viewModel.addObserver(viewLifecycleOwner, viewModel.callNetworkSetting, Observer {
            startActivityForResult(
                Intent(Settings.ACTION_WIRELESS_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                GlobalDefine.REQ_CODE_NETWORK_ON
            )
        })
    }

    private fun quitAppObserving() {
        viewModel.addObserver(viewLifecycleOwner, viewModel.quitApp, Observer {
            quitApp()
        })
    }

    protected fun showToast(@StringRes msg:Int) {
        showToast(getString(msg))
    }

    protected fun showToast(msg:String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun toastObserving() {
        viewModel.addObserver(viewLifecycleOwner, viewModel.toastMessage, Observer {
            showToast(it)
        })

        viewModel.addObserver(viewLifecycleOwner, viewModel.toastMessageString, Observer {
            showToast(it)
        })
    }

    fun showAlert(
        stringResourceId: Int,
        positiveButtonResourceId: Int = bomi.kotlinside.R.string.btn_confirm,
        negativeButtonResourceId: Int = bomi.kotlinside.R.string.btn_cancel,
        lsnDialogButton: DialogInterface.OnClickListener? = null
    ) {
        DAlertParam.Builder()
            .setMessage(stringResourceId)
            .setPositiveButton(positiveButtonResourceId, lsnDialogButton)
            .setNegativeButton(negativeButtonResourceId, lsnDialogButton)
            .create()
            .convertAlertDialogBuilder(mContext)
            .show()
    }

    fun showAlert(
        str: String,
        positiveButtonResourceId: Int = bomi.kotlinside.R.string.btn_confirm,
        negativeButtonResourceId: Int = bomi.kotlinside.R.string.btn_cancel,
        lsnDialogButton: DialogInterface.OnClickListener? = null
    ) {
        DAlertParam.Builder()
            .setMessage(str)
            .setPositiveButton(positiveButtonResourceId, lsnDialogButton)
            .setNegativeButton(negativeButtonResourceId, lsnDialogButton)
            .create()
            .convertAlertDialogBuilder(mContext)
            .show()
    }

    fun showAlertConfirm(
        stringResourceId: Int,
        positiveButtonResourceId: Int = bomi.kotlinside.R.string.btn_confirm,
        lsnDialogButton: DialogInterface.OnClickListener? = null
    ) {
        DAlertParam.Builder()
            .setMessage(stringResourceId)
            .setPositiveButton(positiveButtonResourceId, lsnDialogButton)
            .create()
            .convertAlertDialogBuilder(mContext)
            .show()
    }

    private fun dialogAlertObserving() {
        viewModel.addObserver(viewLifecycleOwner, viewModel.dialogAlertMessage, Observer {
            it.convertAlertDialogBuilder(mContext)
                .show()
        })
        viewModel.addObserver(viewLifecycleOwner, viewModel.dialogAlertMessageString, Observer {
            it.convertAlertDialogBuilder(mContext)
                .show()
        })
    }

    private fun loadingObserving() {
        viewModel.addObserver(viewLifecycleOwner, viewModel.loadingChange, Observer { bShow ->
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

        viewModel.addObserver(viewLifecycleOwner, viewModel.loadingClear, Observer {
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

    fun callLastNetworkTask() {
        viewModel.lastRetryFunction?.invoke()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        var animation = super.onCreateAnimation(transit, enter, nextAnim)
        // HW layer support only exists on API 11+
        if (animation == null && nextAnim != 0) {
            animation = AnimationUtils.loadAnimation(context, nextAnim)
        }
        if (animation != null) {
            view?.let {view->
                if (view.isHardwareAccelerated) {
                    view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                }
            }
            if (isFragmentAddType()) {
                animation.setAnimationListener(lsnTransitAnimation)
            }
        }
        return animation
    }

    fun setTransitAnimationListener(lsnTransitAnimation: Animation.AnimationListener) {
        this.lsnTransitAnimation = lsnTransitAnimation
    }

    /**
     * 프레그먼트 간 주고 받을 데이터 설정
     *
     * @param resultCode 결과코드
     * @param bundle     결과 데이터
     */
    protected fun setResult(resultCode: Int, bundle: Bundle) {
        setResultCode(resultCode)
        setBundleResult(bundle)
    }

    fun getRequestCode(): Int {
        return nRequestCode
    }

    fun setRequestCode(nRequestCode: Int): BaseFragment<*, *> {
        this.nRequestCode = nRequestCode
        return this
    }

    fun getBundleResult(): Bundle? {
        return bundleResult
    }

    fun setBundleResult(bundleResult: Bundle) {
        this.bundleResult = bundleResult
    }

    fun getResultCode(): Int {
        return nResultCode
    }

    fun setResultCode(nResultCode: Int) {
        this.nResultCode = nResultCode
    }

    /**
     * 프레그먼트 간 주고받을 데이터가 있었다면 여기서 처리
     *
     * @param resultCode 결과 코드
     * @param b          결과 데이터
     */
    open fun onReceiveResult(requestCode: Int, resultCode: Int, b: Bundle?) {}

    fun isFragmentAddType(): Boolean  = isFragmentAddType
    fun isFragmentAddHistory() : Boolean = isFragmentAddHistory

    fun setFragmentAddType(flag: Boolean): BaseFragment<*, *> {
        isFragmentAddType = flag
        return this
    }

    fun setFragmentAddHistory(flag:Boolean): BaseFragment<*, *> {
        isFragmentAddHistory = flag
        return this
    }

    fun addFragment(f: BaseFragment<*, *>, addHistory:Boolean=true) {
        getAvailableActivity(object:
            OnActivityEnabledListener {
            override fun onActivityEnabled(activity: BaseActivity<*, *>?) {
                activity?.add(f.setFragmentAddType(true).setFragmentAddHistory(addHistory)) ?: addFragment(f, addHistory)
            }
        })
    }

    fun addFragment(f: BaseFragment<*, *>, requestCode:Int, addHistory:Boolean=true) {
        getAvailableActivity(object:
            OnActivityEnabledListener {
            override fun onActivityEnabled(activity: BaseActivity<*, *>?) {
                activity?.add(f.setFragmentAddType(true).setFragmentAddHistory(addHistory), requestCode) ?: addFragment(f, requestCode, addHistory)
            }
        })
    }

    fun replaceFragment(f: BaseFragment<*, *>, addHistory:Boolean=true) {
        getAvailableActivity(object:
            OnActivityEnabledListener {
            override fun onActivityEnabled(activity: BaseActivity<*, *>?) {
                activity?.add(f.setFragmentAddHistory(addHistory)) ?: replaceFragment(f, addHistory)
            }
        })
    }

    fun replaceFragment(f: BaseFragment<*, *>, requestCode:Int, addHistory:Boolean=true) {
        getAvailableActivity(object:
            OnActivityEnabledListener {
            override fun onActivityEnabled(activity: BaseActivity<*, *>?) {
                activity?.add(f.setFragmentAddHistory(addHistory), requestCode) ?: replaceFragment(f, requestCode, addHistory)
            }
        })
    }

    fun addSingleton(f: BaseFragment<*, *>, clearAllHistory:Boolean = false) {
        getAvailableActivity(object:
            OnActivityEnabledListener {
            override fun onActivityEnabled(activity: BaseActivity<*, *>?) {
                activity?.addSingleton(f, clearAllHistory) ?: addSingleton(f, clearAllHistory)
            }
        })
    }

    fun back() {
        getAvailableActivity(object:
            OnActivityEnabledListener {
            override fun onActivityEnabled(activity: BaseActivity<*, *>?) {
                activity?.popBackStack() ?: back()
            }
        })
    }

    fun popAllFragment() {
        getAvailableActivity(object:
            OnActivityEnabledListener {
            override fun onActivityEnabled(activity: BaseActivity<*, *>?) {
                activity?.popAllFragment() ?: popAllFragment()
            }
        })
    }

    fun popToHome() {
        addSingleton(
            if(mgrPref.flagHaveSaveReport)
                HomeFragment()
            else
                HomeTutorialFragment()
            , clearAllHistory = true
        )
    }

    /**
     * Fragment Refresh Function
     * if need catch Activity.onBackPressed()
     * try set the ((ABase)getActivity()).setBackListener(OnBackPressedListener);
     */
    abstract fun refresh()

    fun finishActivity() {
        getAvailableActivity(object:
            OnActivityEnabledListener {
            override fun onActivityEnabled(activity: BaseActivity<*, *>?) {
                activity?.finish() ?: finishActivity()
            }
        })
    }
}