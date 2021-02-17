package bomi.kotlinside.ui

import android.os.Bundle
import bomi.kotlinside.GlobalDefine
import bomi.kotlinside.R
import bomi.kotlinside.databinding.ActivityMainBinding
import bomi.kotlinside.base.ui.BaseActivity
import bomi.kotlinside.base.ui.BaseDialogFragment
import bomi.kotlinside.base.ui.BaseFragment
import bomi.kotlinside.ui.home.HomeFragment
import bomi.kotlinside.ui.home.HomeTutorialFragment
import bomi.kotlinside.ui.intro.IntroFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_main
    override val viewModel: MainViewModel by viewModel()

    private var nBackPressedTime: Long = 0

    override fun getFragmentFrameId(): Int {
        return R.id.flFragment
    }

    override fun getChildFragmentFrameId(): Int {
        return R.id.flFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        add(IntroFragment().setFragmentAddHistory(false))
    }

    private fun checkAppFinish() {
        val tempTime = System.currentTimeMillis()
        val intervalTime = tempTime - nBackPressedTime

        if (0 < intervalTime && GlobalDefine.FINISH_INTERVAL_TIME >= intervalTime) {
            finishAffinity()
        } else {
            nBackPressedTime = tempTime
            showToast(getString(R.string.toast_finish_app))
        }
    }

    override fun onBackPressed() {
        when (val currentFragment = getLastStackFragment()) {
            is BaseFragment<*, *> -> {
                when (currentFragment) {
                    is HomeFragment, is HomeTutorialFragment -> {
                        if (currentFragment.onBackPressed())
                            checkAppFinish()
                    }
                    is IntroFragment -> {
                        //Hold
                    }
                    else -> {
                        if (currentFragment.onBackPressed())
                            popBackStack()
                    }
                }
            }
            is BaseDialogFragment<*, *> -> {
                if (currentFragment.onBackPressed())
                    popBackStack()
            }
            else -> super.onBackPressed()
        }
    }

}
