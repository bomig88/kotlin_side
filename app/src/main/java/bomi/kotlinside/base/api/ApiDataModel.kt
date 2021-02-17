package bomi.kotlinside.base.api

import bomi.kotlinside.api.res.VisitorJejuVO
import io.reactivex.Single
import retrofit2.Response

interface ApiDataModel {
    fun getVisitorJeju(
        key:String,
        startDate:String,
        endDate:String,
        nationality:String
    ) : Single<Response<VisitorJejuVO>>
}