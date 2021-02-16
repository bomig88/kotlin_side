package bomi.kotlinside.ui.base

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import bomi.kotlinside.GlobalDefine
import bomi.kotlinside.ui.dialog.DLoading
import bomi.kotlinside.util.PreferenceUtil
import org.koin.android.ext.android.inject

@Suppress("unused")
abstract class BaseActivity<T : ViewDataBinding, R : BaseViewModel> : AppCompatActivity() {
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

    protected val mgrPref: PreferenceUtil by inject()
    protected lateinit var mFragmentHelper : FragmentHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialogLoading = DLoading(this@BaseActivity)

        mFragmentHelper = FragmentHelper(this)

        viewDataBinding = DataBindingUtil.setContentView(this, layoutResourceId)
        viewDataBinding.lifecycleOwner = this

        toastObserving()
        dialogAlertObserving()
        loadingObserving()
        callNetworkSettingObserving()
        quitAppObserving()
    }

    protected fun showToast(@StringRes msg: Int) {
        showToast(getString(msg))
    }

    protected fun showToast(msg: String) {
        Toast.makeText(this@BaseActivity, msg, Toast.LENGTH_SHORT).show()
    }

    private fun callNetworkSettingObserving() {
        viewModel.addObserver(this, viewModel.callNetworkSetting, Observer {
            startActivityForResult(
                Intent(Settings.ACTION_WIRELESS_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                GlobalDefine.REQ_CODE_NETWORK_ON
            )
        })
    }

    private fun quitAppObserving() {
        viewModel.addObserver(this, viewModel.quitApp, Observer {
            finishAffinity()
        })
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
            it.convertAlertDialogBuilder(this@BaseActivity)
                .show()
        })
        viewModel.addObserver(this, viewModel.dialogAlertMessageString, Observer {
            it.convertAlertDialogBuilder(this@BaseActivity)
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

    abstract fun getFragmentFrameId(): Int

    open fun getChildFragmentFrameId(): Int {
        return 0
    }

    protected open fun getBackStackSize(): Int {
        return mFragmentHelper.getBackStackSize()
    }

    open fun getLastStackFragment(): BaseFragment<*,*>? {
        return mFragmentHelper.getLastStackFragment()
    }

    /**
     * 홈 화면 전용 프레그먼트 관리코드
     * 동일한 클래스의 프레그먼트가 오면 기존 스택에서 제거함 (자식 클래스도 같이 제거)
     * 프레그먼트 전환 애니메이션 세팅 있음
     *
     * @param f : 전환할 프레그먼트
     */
    open fun addSingleton(f: BaseFragment<*,*>, clearAllHistory:Boolean = false) {
        mFragmentHelper.addSingleton(f, clearAllHistory)
    }

    /**
     * 스택에 있는 모든 프레그먼트를 종료하고 스택 초기화
     */
    open fun popAllFragment() {
        mFragmentHelper.popAllFragment()
    }

    /**
     * 프레그먼트 적재
     *
     * @param f 실을 프레그먼트
     */
    open fun add(f: BaseFragment<*,*>) {
        add(f, BaseFragment.REQUEST_EMPTY)
    }

    /**
     * 프레그먼트 적재
     * 자식 프레그먼트일 경우에는 add, 그 외는 replace 진행
     * 프레그먼트 전환 애니메이션 세팅 있음
     *
     * @param f         실을 프레그먼트
     * @param requestId requestCode 값
     */
    open fun add(f: BaseFragment<*,*>, requestId: Int) {
        mFragmentHelper.add(f, requestId)
    }

    /**
     * 프레그먼트 스택의 가장 마지막 아이템 삭제 (맨 위에 보이는 프레그먼트)
     * 자식 프레그먼트일 경우에는 remove, 그 외는 replace 진행
     * 하위 프레그먼트에 보낼 데이터가 있다면 확인 후 보냄
     * 프레그먼트 전환 애니메이션 세팅 있음
     */
    open fun popBackStack() {
        mFragmentHelper.popBackStack()
    }

    open fun getFlagHaveSaveReport() = mgrPref.flagHaveSaveReport
}