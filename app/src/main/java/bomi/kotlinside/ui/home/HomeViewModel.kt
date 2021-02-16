package bomi.kotlinside.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bomi.kotlinside.api.ApiDataModel
import bomi.kotlinside.ui.base.BaseApiViewModel

class HomeViewModel(private val apiModule : ApiDataModel) : BaseApiViewModel(apiModule) {
    var pageCount = 0
    var currentPage = 0

    var flagLoadingList = false
}