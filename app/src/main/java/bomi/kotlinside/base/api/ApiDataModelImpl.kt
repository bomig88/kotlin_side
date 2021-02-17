package bomi.kotlinside.base.api

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response

class ApiDataModelImpl(private val service: ApiService) : ApiDataModel {
    override fun getXml(): Single<Response<ResponseBody>> {
        return service.getXml()
    }
}