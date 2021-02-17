package bomi.kotlinside.base.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import bomi.kotlinside.R
import bomi.kotlinside.ui.home.HomeFragment
import bomi.kotlinside.ui.home.HomeTutorialFragment
import bomi.kotlinside.ui.home.PopupFragment
import java.util.*

class FragmentHelper(private val mActivity: BaseActivity<*, *>) {
    //---- Fragment Animation ----
    enum class AnimFragmentTransaction {
        NONE
        , SLIDE_IN_FROM_LEFT, SLIDE_IN_FROM_RIGHT
        , SLIDE_IN_FROM_BOTTOM, SLIDE_IN_FROM_LEFT_OUT_TO_RIGHT
        , SLIDE_IN_FROM_RIGHT_OUT_TO_LEFT, SLIDE_OUT_TO_LEFT
        , SLIDE_OUT_TO_RIGHT, SLIDE_OUT_TO_BOTTOM
    }
    //----------------------------

    //----------------------------
    /**
     * 프레그먼트 스택
     */
    private val mMapStack = ArrayList<BaseFragment<*, *>>()

    /**
     * add 형태의 자식 플래그먼트의 전환 애니메이션이 종료된 후 뒷배경 뷰 제거
     */
    fun getBackStackSize(): Int {
        return mMapStack.size
    }

    fun getLastStackFragment(): BaseFragment<*, *>? {
        return if (getBackStackSize() > 0) mMapStack[mMapStack.size - 1] else null
    }

    /**
     * 홈 화면 전용 프레그먼트 관리코드
     * 동일한 클래스의 프레그먼트가 오면 기존 스택에서 제거함 (자식 클래스도 같이 제거)
     * 프레그먼트 전환 애니메이션 세팅 있음
     *
     * @param f : 전환할 프레그먼트
     */
    fun addSingleton(f: BaseFragment<*, *>, clearAllHistory:Boolean = false) {
        val mgrFragment =
            mActivity.supportFragmentManager
        val beginTransaction =
            mgrFragment.beginTransaction()
        val fClass: Class<*> = f::class.java
        val arrRemover: ArrayList<BaseFragment<*, *>> = ArrayList()
        var isFromBackStack = false
        if(clearAllHistory) {
            arrRemover.addAll(mMapStack)
        } else {
            for (saveF in mMapStack) {
                if ((fClass == saveF::class.java || saveF.isFragmentAddType())) {
                    arrRemover.add(saveF)
                }
            }
        }
        if (arrRemover.size > 0) isFromBackStack = true
        for (removeF in arrRemover) {
            beginTransaction.remove(removeF)
            mMapStack.remove(removeF)
        }
        mMapStack.trimToSize()
        arrRemover.clear()
        mMapStack.add(f)
        setTransactionAnim(beginTransaction, f, isFromBackStack)
        beginTransaction.replace(mActivity.getFragmentFrameId(), f).commitAllowingStateLoss()
        //mgrFragment.executePendingTransactions()
    }

    /**
     * 스택에 있는 모든 프레그먼트를 종료하고 스택 초기화
     */
    fun popAllFragment() {
        val mgrFragment =
            mActivity.supportFragmentManager
        val beginTransaction =
            mgrFragment.beginTransaction()

        if (mMapStack.size > 1) {
            val arrRemover: ArrayList<BaseFragment<*, *>> = ArrayList()
            for (f in mMapStack) {
                val compareFragmentClass: Class<*> = if (mActivity.getFlagHaveSaveReport())
                    HomeFragment::class.java
                else
                    HomeTutorialFragment::class.java
                if((f::class.java != compareFragmentClass))
                    arrRemover.add(f)
            }
            for (f in arrRemover) {
                mMapStack.remove(f)
                beginTransaction.remove(f)
            }
            mMapStack.trimToSize()
        }

        beginTransaction.replace(mActivity.getFragmentFrameId(), mMapStack[0])
        beginTransaction.commitAllowingStateLoss()
        //mgrFragment.executePendingTransactions()
    }

    fun add(f: BaseFragment<*, *>, requestId: Int) {
        val mgrFragment =
            mActivity.supportFragmentManager
        if (requestId != BaseFragment.REQUEST_EMPTY) {
            var reqId: Bundle? = f.arguments
            if (reqId == null) reqId = Bundle()
            reqId.putInt(BaseFragment.REQUEST_ID, requestId)
            f.arguments = reqId
        }
        mMapStack.add(f)
        val beginTransaction =
            mgrFragment.beginTransaction()
        setTransactionAnim(beginTransaction, f, false)
        if (f.isFragmentAddType()) {
            beginTransaction.add(mActivity.getChildFragmentFrameId(), f)
        } else {
            beginTransaction.replace(mActivity.getFragmentFrameId(), f)
        }
        beginTransaction.commitAllowingStateLoss()
        //mgrFragment.executePendingTransactions()
    }

    fun popBackStack() {
        val mgrFragment =
            mActivity.supportFragmentManager
        if (mMapStack.size > 0) {
            val lastPos = mMapStack.size - 1
            val popFragment: BaseFragment<*, *> = mMapStack[lastPos]
            val requestCode: Int = popFragment.getRequestCode()
            val resultCode: Int = popFragment.getResultCode()
            val bundleData: Bundle? = popFragment.getBundleResult()
            if ((popFragment is HomeTutorialFragment || popFragment is HomeFragment)) {
                mMapStack.clear()
                popFragment.finishActivity()
            } else {
                mMapStack.removeAt(lastPos)
                mMapStack.trimToSize()
                val beginTransaction =
                    mgrFragment.beginTransaction()
                //val animType: FRAGMENT_ANIM = FRAGMENT_ANIM.NONE
                setTransactionAnim(beginTransaction, popFragment, true)
                if (mMapStack.size > 0) {
                    var redrawPos = -1
                    val stackSize = mMapStack.size - 1

                    //History 에 프래그먼트를 남기지 않는 경우가 있으므로 다시 그릴 하위 프래그먼트를 찾음
                    for(idx in stackSize downTo 0) {
                        if(mMapStack[idx].isFragmentAddHistory()) {
                            redrawPos = idx
                            break
                        }
                    }

                    //다시 그릴 하위 프래그먼트가 없으므로 앱을 종료함
                    if(redrawPos == -1) {
                        mActivity.finishAffinity()

                    } else {
                        for (idx in stackSize until redrawPos - 1) {
                            mMapStack.removeAt(idx)
                        }
                        mMapStack.trimToSize()

                        val redrawFragment: BaseFragment<*, *> = mMapStack[redrawPos]

                        if (popFragment.isFragmentAddType()) {
                            beginTransaction.remove(popFragment)
                        } else {
                            beginTransaction.replace(mActivity.getFragmentFrameId(), redrawFragment)
                        }
                        beginTransaction.commitAllowingStateLoss()
                        //mgrFragment.executePendingTransactions()

                        if (requestCode != BaseFragment.REQUEST_EMPTY) redrawFragment.onReceiveResult(
                            requestCode,
                            resultCode,
                            bundleData
                        ) else redrawFragment.refresh()
                    }

                } else {
                    beginTransaction.remove(popFragment).commitAllowingStateLoss()
                    //mgrFragment.executePendingTransactions()
                }
            }
        }
    }

    private fun getAnimateType(
        f: Fragment?,
        isReverse: Boolean
    ): AnimFragmentTransaction {
        return if (f == null) {
            AnimFragmentTransaction.NONE

        } else if (f is PopupFragment
        ) {
            if (isReverse) AnimFragmentTransaction.SLIDE_OUT_TO_RIGHT else AnimFragmentTransaction.SLIDE_IN_FROM_RIGHT

        } else {
            AnimFragmentTransaction.NONE
        }
    }

    private fun setTransactionAnim(
        beginTransaction: FragmentTransaction,
        f: Fragment?,
        isReverse: Boolean
    ): AnimFragmentTransaction {
        val animResIn: Int
        val animResOut: Int
        val animType: AnimFragmentTransaction = getAnimateType(f, isReverse)
        when (animType) {
            AnimFragmentTransaction.NONE -> {
                animResIn = R.anim.hold
                animResOut = R.anim.hold
            }
            AnimFragmentTransaction.SLIDE_IN_FROM_LEFT -> {
                animResIn = R.anim.slide_in_from_left
                animResOut = R.anim.hold
            }
            AnimFragmentTransaction.SLIDE_IN_FROM_RIGHT -> {
                animResIn = R.anim.slide_in_from_right
                animResOut = R.anim.hold
            }
            AnimFragmentTransaction.SLIDE_IN_FROM_LEFT_OUT_TO_RIGHT -> {
                animResIn = R.anim.slide_in_from_left
                animResOut = R.anim.slide_out_to_right
            }
            AnimFragmentTransaction.SLIDE_IN_FROM_RIGHT_OUT_TO_LEFT -> {
                animResIn = R.anim.slide_in_from_right
                animResOut = R.anim.slide_out_to_left
            }
            AnimFragmentTransaction.SLIDE_IN_FROM_BOTTOM -> {
                animResIn = R.anim.slide_in_from_bottom
                animResOut = R.anim.hold
            }
            AnimFragmentTransaction.SLIDE_OUT_TO_LEFT -> {
                animResIn = R.anim.hold
                animResOut = R.anim.slide_out_to_left
            }
            AnimFragmentTransaction.SLIDE_OUT_TO_RIGHT -> {
                animResIn = R.anim.hold
                animResOut = R.anim.slide_out_to_right
            }
            AnimFragmentTransaction.SLIDE_OUT_TO_BOTTOM -> {
                animResIn = R.anim.hold
                animResOut = R.anim.slide_out_to_bottom
            }
        }
        beginTransaction.setCustomAnimations(animResIn, animResOut, animResIn, animResOut)
        return animType
    }
}