package bomi.kotlinside.api.res

data class VisitorJejuVO(
    val totCnt:Int,
    val hasMore:Boolean,
    val data:ArrayList<VisitorVO>?
) {
    data class VisitorVO(
        val dtYearMonth:String,
        val nationality:String,
        val visitorCnt:Int
    )
}