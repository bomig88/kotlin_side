package bomi.kotlinside.ui.home

import android.os.Bundle
import android.view.View
import bomi.kotlinside.databinding.FragmentHomeTutorialBinding
import bomi.kotlinside.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 메인화면1
 * 저장된 견적 정보가 하나도 없을 경우 노출
 */
class HomeTutorialFragment : BaseFragment<FragmentHomeTutorialBinding, HomeTutorialViewModel>() {
    override val layoutResourceId: Int = bomi.kotlinside.R.layout.fragment_home_tutorial
    override val viewModel: HomeTutorialViewModel by viewModel()

    override fun onBackPressed(): Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewDataBinding.vm = viewModel
        if(activityViewModel.hasPopupList())
            addFragment(PopupFragment())
    }

    override fun refresh() { }
}