package bomi.kotlinside.ui.intro

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import bomi.kotlinside.GlobalDefine
import bomi.kotlinside.R
import bomi.kotlinside.base.ui.BaseFragment
import bomi.kotlinside.databinding.FragmentIntroBinding
import bomi.kotlinside.ui.home.HomeFragment
import bomi.kotlinside.util.Log
import bomi.kotlinside.util.SecureUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 앱 최초 구동 스플래시 화면
 */
class IntroFragment : BaseFragment<FragmentIntroBinding, IntroViewModel>() {
    override val layoutResourceId: Int = R.layout.fragment_intro
    override val viewModel: IntroViewModel by viewModel()

    override fun onBackPressed(): Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.addObserver(viewLifecycleOwner, viewModel.visitorJeju, Observer {
            Log.d("testLog", "visitorJeju :: ${it?.data?.size ?:"null"}")
            activityViewModel.visitorJejuVO = it

            mgrPref.flagHaveSaveReport = true

//            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.w("TAG", "Fetching FCM registration token failed")
//                    return@OnCompleteListener
//                }
//
//                // Get new FCM registration token
//                val token = task.result
//
//                // Log and toast
//                val msg = "token :: $token"
//                Log.d("TAG", msg)
//                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
//            })

            //앱 최초 실행일 경우 튜토리얼 화면으로 이동
            //아닐 경우 핀번호 화면으로 이동
            if(mgrPref.flagFirstAppStart)
                replaceFragment(HomeFragment(), addHistory = true)
        })

        if(checkPlayServices()) {
            viewDataBinding.root.postDelayed({
                try {
                    if (SecureUtil.isRooting(mContext)) {
                        AlertDialog.Builder(mContext)
                            .setCancelable(false)
                            .setMessage(R.string.ignore_apk)
                            .setPositiveButton(
                                R.string.btn_quit
                            ) { _, _ -> finishActivity() }
                            .show()
                    } else {
                        viewModel.getVisitorJeju(getString(R.string.project_key),
                            "201805",
                            "201905",
                            "")
                    }
                } catch (e: Exception) {
                    Log.e("LogError", "error::" , e)
                    showAlertQuit()
                }
            }, 2000)
        }
    }

    /**
     * 구글 플레이 스토어 상태 체크
     *
     * @return true / false
     */
    private fun checkPlayServices(): Boolean {
        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(mContext)
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(
                    this@IntroFragment, result,
                    GlobalDefine.PLAY_SERVICES_RESOLUTION_REQUEST
                )?.let {dialog->
                    dialog.setOnDismissListener { finishActivity() }
                    dialog.show()
                } ?:finishActivity()
            }
            return false
        }
        return true
    }

    private fun showAlertQuit() {
        AlertDialog.Builder(mContext)
            .setCancelable(false)
            .setMessage(R.string.ignore_apk)
            .setPositiveButton(
                R.string.btn_quit
            ) { _, _ -> finishActivity() }
            .show()
    }

    /**
     * market Version 형식 맞추기
     *
     * @param lengthBigger 길이가 더 긴 버전
     * @param changer      길이가 더 짧은 버전
     * @return 변경된 버전값 반환 (31, 228) 이 들어오면 310이 돌아감
     */
    private fun matchVersionLengthBigger(
        lengthBigger: String,
        changer: String
    ): String {
        val version = StringBuilder(changer)

        for (idx in changer.length until lengthBigger.length) {
            version.append(getString(R.string.default_version_string))
        }

        return version.toString()
    }

    /**
     * 버전 체크 후 업데이트 버전 있을 시 구글 마켓 오픈
     */
    private fun openGoogleMarket() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.market_scheme) + mContext.packageName)
                )
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (err: android.content.ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.market_url) + mContext.packageName)
                )
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    override fun refresh() { }

}
