package bomi.kotlinside.base.api

import bomi.kotlinside.base.api.BaseResponse
import bomi.kotlinside.api.res.ResPopupVO
import io.reactivex.Single

interface ApiDataModel {

    fun getPopupList(serviceType:String) : Single<BaseResponse<ResPopupVO>>
}