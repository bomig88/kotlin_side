package bomi.kotlinside.base.api

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response

interface ApiDataModel {
    fun getXml() : Single<Response<ResponseBody>>
}