package bomi.kotlinside.api.res

import bomi.kotlinside.api.deserialization.PopupDeserialization
import com.google.gson.annotations.JsonAdapter

data class ResPopupVO(var popupList: ArrayList<PopupVO>?) {
    @JsonAdapter(PopupDeserialization::class)
    data class PopupVO(
        var id: Long
        , var title: String?
        , var content: String?
        , var todayHideActiveBool: Boolean
        , var hideActiveBool: Boolean
    )
}