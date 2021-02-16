package bomi.kotlinside.api

import bomi.kotlinside.api.res.BaseResponse
import bomi.kotlinside.api.res.ResPopupVO
import io.reactivex.Single
import retrofit2.http.*

interface ApiService {

    @GET("common/popup")
    fun getPopupList(@Query("serviceType") serviceType:String) : Single<BaseResponse<ResPopupVO>>
}