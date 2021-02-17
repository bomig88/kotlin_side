package bomi.kotlinside.base.api

import bomi.kotlinside.api.res.VisitorJejuVO
import io.reactivex.Single
import retrofit2.Response

class ApiDataModelImpl(private val service: ApiService) : ApiDataModel {
    override fun getVisitorJeju(
        key:String,
        startDate:String,
        endDate:String,
        nationality:String
    ): Single<Response<VisitorJejuVO>> {
        return service.getVisitorJeju(key, startDate, endDate, nationality)
    }
}