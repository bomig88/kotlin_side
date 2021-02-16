package bomi.kotlinside.ui.home

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.lifecycle.Observer
import bomi.kotlinside.R
import bomi.kotlinside.api.res.ResPopupVO
import bomi.kotlinside.databinding.FragmentPopupBinding
import bomi.kotlinside.ui.base.BaseFragment
import bomi.kotlinside.util.CommonUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 약관 상세 보기 화면
 */
class PopupFragment : BaseFragment<FragmentPopupBinding, PopupViewModel>() {
    override val layoutResourceId: Int = R.layout.fragment_popup
    override val viewModel: PopupViewModel by viewModel()

    override fun onBackPressed(): Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.addObserver(viewLifecycleOwner, viewModel.clickClose, Observer {
            viewDataBinding.data?.let{ data->
                if(data.todayHideActiveBool && viewDataBinding.cbNotSeeToday.isChecked) { //오늘 하루 보지 않기 체크
                    activityViewModel.putNotSeeToday(data, prefUtil = mgrPref)
                }
                if(data.hideActiveBool && viewDataBinding.cbNotSeeAfter.isChecked) { //다시 보지 않기 체크
                    activityViewModel.putNotSeeAfter(data, prefUtil = mgrPref)
                }
            }

            activityViewModel.currentArrayPosition++

            activityViewModel.getContent()?.let {
                setContent(it)
            } ?: clearPopupListAndBack()
        })

        //Touch 영역이 childe Fragment로 가지 않도록 막음
        viewDataBinding.root.setOnClickListener{}

        viewDataBinding.wvMain.setInitialScale(1)

        viewDataBinding.wvMain.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        viewDataBinding.wvMain.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        viewDataBinding.wvMain.webChromeClient = WebChromeClient()
        viewDataBinding.wvMain.webViewClient = WebClientDetail(activity)
        viewDataBinding.wvMain.settings.useWideViewPort = true
        viewDataBinding.wvMain.settings.loadWithOverviewMode = true
        viewDataBinding.wvMain.settings.domStorageEnabled = true
        viewDataBinding.wvMain.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        viewDataBinding.wvMain.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        viewDataBinding.wvMain.settings.defaultFontSize = resources.getInteger(R.integer.web_default_font_size)

        CookieManager.getInstance().setAcceptThirdPartyCookies(viewDataBinding.wvMain, true)

        viewDataBinding.vm = viewModel

        activityViewModel.getContent()?.let {
            setContent(it)
        } ?: back()
    }

    private fun clearPopupListAndBack() {
        activityViewModel.clearPopupList()
        back()
    }

    private fun setContent(data: ResPopupVO.PopupVO) {
        viewDataBinding.data = data

        viewDataBinding.wvMain.loadData(
            CommonUtil.getFitScreenHtml(data.content ?:""),
            "text/html; charset=utf-8;",
            "UTF-8"
        )
        viewDataBinding.cbNotSeeAfter.isChecked = false
        viewDataBinding.cbNotSeeToday.isChecked = false
    }

    internal class WebClientDetail(private val activity: Activity?) : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            val uri = Uri.parse(url)
            return handleUri(uri)
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            val uri = request.url
            return handleUri(uri)
        }

        private fun handleUri(uri: Uri?): Boolean {
            return uri?.let {
                if (CommonUtil.parseSchemeForOutBrowser(activity = activity, uri = uri)) {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = uri
                    activity?.startActivity(i)
                    true
                } else {
                    false
                }
            } ?:false
        }
    }

    override fun refresh() { }
}