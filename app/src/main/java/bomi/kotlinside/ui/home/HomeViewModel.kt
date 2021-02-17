package bomi.kotlinside.ui.home

import bomi.kotlinside.base.api.ApiDataModel
import bomi.kotlinside.base.ui.viewmodel.BaseApiViewModel

class HomeViewModel(private val apiModule : ApiDataModel) : BaseApiViewModel(apiModule) {
    var pageCount = 0
    var currentPage = 0

    var flagLoadingList = false
}