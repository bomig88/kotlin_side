package bomi.kotlinside.api

import bomi.kotlinside.api.res.BaseResponse
import bomi.kotlinside.api.res.ResPopupVO
import io.reactivex.Single

interface ApiDataModel {

    fun getPopupList(serviceType:String) : Single<BaseResponse<ResPopupVO>>
}