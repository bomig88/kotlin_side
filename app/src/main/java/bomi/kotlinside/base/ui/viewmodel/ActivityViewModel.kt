package bomi.kotlinside.base.ui.viewmodel

import bomi.kotlinside.api.res.VisitorJejuVO
import java.text.SimpleDateFormat
import java.util.*

class ActivityViewModel : BaseViewModel() {
    var formatYYYYMMDD : SimpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    var visitorJejuVO : VisitorJejuVO? = null
}