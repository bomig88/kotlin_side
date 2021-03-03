package bomi.kotlinside.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import bomi.kotlinside.R
import bomi.kotlinside.databinding.FragmentHomeBinding
import bomi.kotlinside.base.ui.BaseFragment
import bomi.kotlinside.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.StringBuilder

/**
 * 메인화면 2
 * 저장된 견적 정보가 하나라도 있을 경우 노출
 */
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override val layoutResourceId: Int = R.layout.fragment_home
    override val viewModel: HomeViewModel by viewModel()

    override fun onBackPressed(): Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.tvResult.isVerticalScrollBarEnabled = true
        viewDataBinding.tvResult.movementMethod = ScrollingMovementMethod()

        activityViewModel.visitorJejuVO?.let {
            val builder = StringBuilder()
            builder.append("totalCnt = ${it.totCnt}").append("\n")
            it.data?.let { visitors->
                for(visitor in visitors.iterator()) {
                    builder.append("dtYearMonth = ${visitor.dtYearMonth}, nationality = ${visitor.nationality}, visitorCnt = ${visitor.visitorCnt}").append("\n")
                }
            }

            viewDataBinding.tvResult.text = builder.toString()
        }

        FirebaseAuth.getInstance().currentUser?.let {
            Log.d("TAGGGGG", "already login // " + it.email)
            Toast.makeText(mContext, "login!! " + it.email, Toast.LENGTH_SHORT).show()

        } ?: registerForActivityResult(object : ActivityResultContract<ArrayList<AuthUI.IdpConfig>, FirebaseUser?>() {
            override fun createIntent(context: Context, input: ArrayList<AuthUI.IdpConfig>): Intent {
                return AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(input)
                    .build()
            }

            override fun parseResult(resultCode: Int, intent: Intent?): FirebaseUser? {
                return when (resultCode) {
                    Activity.RESULT_OK -> FirebaseAuth.getInstance().currentUser
                    else -> null
                }
            }

        }) {
            it?.let {
                Log.d("TAGGGGG", "login success // " + it.email)
                Toast.makeText(mContext, "login!! " + it.email, Toast.LENGTH_SHORT).show()
            }
        }.launch(arrayListOf(
//            AuthUI.IdpConfig.EmailBuilder().build(),
//            AuthUI.IdpConfig.PhoneBuilder().build(),
//            AuthUI.IdpConfig.FacebookBuilder().build(),
//            AuthUI.IdpConfig.TwitterBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        )
    }

    override fun refresh() { }
}
