package bomi.kotlinside.api.deserialization

//class SampleDeserialization : JsonDeserializer<ResPopupVO.PopupVO> {
//    override fun deserialize(
//        json: JsonElement?,
//        typeOfT: Type?,
//        context: JsonDeserializationContext?
//    ): ResPopupVO.PopupVO {
//
//        val jsonObject = json?.asJsonObject ?: throw NullPointerException("Response Json String is null")
//
//        val id = jsonObject["id"]
//        val title = jsonObject["title"]
//        val content = jsonObject["content"]
//        val todayHideActiveBool = jsonObject["todayHideActiveBool"]
//        val hideActiveBool = jsonObject["hideActiveBool"]
//
//        return ResPopupVO.PopupVO(
//            id?.asLong ?:0
//            , title?.asString ?:""
//            , content?.asString ?:""
//            , todayHideActiveBool?.asInt == 1
//            , hideActiveBool?.asInt == 1
//        )
//    }
//}