package bomi.kotlinside.api

import bomi.kotlinside.api.res.BaseResponse
import bomi.kotlinside.api.res.ResPopupVO
import io.reactivex.Single

class ApiDataModelImpl(private val service: ApiService) : ApiDataModel {

    override fun getPopupList(serviceType: String): Single<BaseResponse<ResPopupVO>> {
        return service.getPopupList(serviceType)
    }
}