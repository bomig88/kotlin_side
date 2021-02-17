package bomi.kotlinside.ui.home

import android.os.Bundle
import android.view.View
import bomi.kotlinside.R
import bomi.kotlinside.databinding.FragmentHomeBinding
import bomi.kotlinside.base.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

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
    }

    override fun refresh() { }
}
