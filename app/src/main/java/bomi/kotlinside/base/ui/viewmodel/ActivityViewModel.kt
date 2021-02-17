package bomi.kotlinside.base.ui.viewmodel

import java.text.SimpleDateFormat
import java.util.*

class ActivityViewModel : BaseViewModel() {
    var formatYYYYMMDD : SimpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
}