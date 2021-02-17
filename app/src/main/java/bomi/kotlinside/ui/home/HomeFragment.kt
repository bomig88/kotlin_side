package bomi.kotlinside.ui.home

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import bomi.kotlinside.R
import bomi.kotlinside.databinding.FragmentHomeBinding
import bomi.kotlinside.base.ui.BaseFragment
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
    }

    override fun refresh() { }
}
