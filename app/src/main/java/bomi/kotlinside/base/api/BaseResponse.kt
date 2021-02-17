package bomi.kotlinside.base.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Suppress("unused")
class BaseResponse<T>{
    inner class Status{
        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("description")
        @Expose
        var description: String? = null

        @SerializedName("code")
        @Expose
        var code: String? = null
    }

    @SerializedName("status")
    @Expose
    var status: Status? = null

    @SerializedName("data")
    @Expose
    var data: T? = null

    val message: String
        get() = (status?.message ?:"")

    val resCodeIsSuccess: Boolean
        get() = (status?.code != null && status?.code.equals("0000"))
}