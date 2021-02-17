package bomi.kotlinside.base.api

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("webhp")
    fun getXml() : Single<Response<ResponseBody>>
}