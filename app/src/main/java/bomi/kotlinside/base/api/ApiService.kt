package bomi.kotlinside.base.api

import bomi.kotlinside.api.res.VisitorJejuVO
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    //제주데이터허브
    //https://www.jejudatahub.net/data/view/data/POPULATION/582
    @GET("api/proxy/tbata552bat15Daa585t52D5a5t818t2/{key}")
    fun getVisitorJeju(
        @Path("key") key:String,
        @Query("startDate") startDate:String,
        @Query("endDate") endDate:String,
        @Query("nationality") nationality:String
    ) : Single<Response<VisitorJejuVO>>
}