package bomi.kotlinside.ui.base

import bomi.kotlinside.api.res.ResPopupVO
import bomi.kotlinside.util.PreferenceUtil
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.util.*

class ActivityViewModel : BaseViewModel() {
    var formatYYYYMMDD : SimpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    private var jsonNotSeeToday: JsonObject? = null
    private var jsonNotSeeAfter: JsonObject? = null

    fun initPopup(prefUtil: PreferenceUtil, popupList: ArrayList<ResPopupVO.PopupVO>) {
        jsonNotSeeToday = Gson().fromJson(prefUtil.jsonNotSeeToday
            , JsonObject::class.java)
        jsonNotSeeAfter = Gson().fromJson(prefUtil.jsonNotSeeAfter
            , JsonObject::class.java)

        arrayPopup = popupList

        filteringPopup()
    }

    fun putNotSeeToday(data: ResPopupVO.PopupVO, prefUtil: PreferenceUtil) {
        jsonNotSeeToday?.addProperty(
            data.id.toString(),
            formatYYYYMMDD.format(Calendar.getInstance().time)
        )
        prefUtil.jsonNotSeeToday = jsonNotSeeToday?.toString() ?:"{}"
    }

    fun putNotSeeAfter(data: ResPopupVO.PopupVO, prefUtil: PreferenceUtil) {
        jsonNotSeeAfter?.addProperty(
            data.id.toString(),
            formatYYYYMMDD.format(Calendar.getInstance().time)
        )
        prefUtil.jsonNotSeeAfter = jsonNotSeeAfter?.toString() ?: "{}"
    }

    private lateinit var arrayPopup : ArrayList<ResPopupVO.PopupVO>
    var currentArrayPosition = 0
    fun getContent() : ResPopupVO.PopupVO? {
        return try {
            arrayPopup[currentArrayPosition]
        } catch (e: Exception) {
            null
        }
    }

    fun hasPopupList(): Boolean {
        return arrayPopup.isNotEmpty()
    }

    fun clearPopupList() {
        arrayPopup.clear()
    }

    private fun filteringPopup() {
        val filteringArray = ArrayList<ResPopupVO.PopupVO>()

        for(index in 0 until arrayPopup.size) {
            val key = arrayPopup[index].id.toString()

            if(jsonNotSeeAfter?.has(key) == true) {
                filteringArray.add(arrayPopup[index])

            } else if(jsonNotSeeToday?.has(key) == true) {
                val saveDate = jsonNotSeeToday!![key].asString
                if(saveDate == formatYYYYMMDD.format(Calendar.getInstance().time)) {
                    filteringArray.add(arrayPopup[index])
                }
            }
        }

        if(filteringArray.isNotEmpty()) {
            for(item in filteringArray) {
                arrayPopup.remove(item)
            }
            arrayPopup.trimToSize()
        }
    }
}