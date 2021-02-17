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

//        savedInstanceState?.let {
//            if(savedInstanceState.containsKey(getString(R.string.p_count))) {
//                viewModel.pageCount = savedInstanceState.getInt(getString(R.string.p_page))
//            }
//            if(savedInstanceState.containsKey(getString(R.string.p_page))) {
//                viewModel.currentPage = savedInstanceState.getInt(getString(R.string.p_page))
//            }
//        }

        //홈 화면 갱신
//        activityViewModel.addObserver(viewLifecycleOwner, activityViewModel.refreshView, Observer {
//            requestList()
//        })

        //견적 목록 획득 - 견적이 없을 경우 메인화면 1 / HomeTutorial 로 이동
//        viewModel.addObserver(viewLifecycleOwner, viewModel.reportListVO, Observer {
//            if(it.size == 0) {
//                mgrPref.flagHaveSaveReport = false
//                popToHome()
//
//            } else {
//                viewDataBinding.srlRefresh.isRefreshing = false
//
//                viewDataBinding.pager.run {
//                    adapter = HomeAdapter(this@HomeFragment).apply {
//                        setArrayPager(it)
//                    }
//
//                    if(it.size == viewModel.pageCount
//                        && viewModel.currentPage != 0) {
//                        viewDataBinding.pager.currentItem = viewModel.currentPage
//                    }
//
//                    viewModel.pageCount = it.size
//                    viewModel.currentPage = viewModel.currentPage
//                }
//
//                if(activityViewModel.hasPopupList())
//                    addFragment(PopupFragment())
//            }
//        })

        if(activityViewModel.hasPopupList())
            addFragment(PopupFragment())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        outState.putInt(getString(R.string.p_count), viewDataBinding.pager.adapter?.itemCount ?:0)
//        outState.putInt(getString(R.string.p_page), viewDataBinding.pager.currentItem)
    }

//    private val callbackPagerChange = object : ViewPager2.OnPageChangeCallback() {
//        override fun onPageSelected(position: Int) {
//            super.onPageSelected(position)
//            viewModel.currentPage = position
//        }
//    }

    override fun refresh() { }
}
