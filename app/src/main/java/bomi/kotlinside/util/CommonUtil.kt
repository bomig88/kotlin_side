package bomi.kotlinside.util

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import bomi.kotlinside.BuildConfig
import com.google.gson.Gson
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@Suppress("MemberVisibilityCanBePrivate", "unused")
object CommonUtil {
    private val TAG = CommonUtil::class.java.simpleName
    /**
     * Formatter
     */
    var formatYYYYMMDD : SimpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    const val koreanNameFilterPattern = "^[가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025\\u00B7\\uFE55]*$"
    var jsonParser = Gson()

    @Suppress("DEPRECATION")
    @JvmStatic
    fun getHtmlTagString(htmlTagString: String?): Spanned {
        return if (TextUtils.isEmpty(htmlTagString)) {
            SpannableStringBuilder("")
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                Html.fromHtml(htmlTagString)
            } else {
                Html.fromHtml(htmlTagString, Html.FROM_HTML_MODE_LEGACY)
            }
        }
    }

    /**
     * 소프트 키패드 숨기기
     *
     * @param context 현재 노출 중인 context / Activity
     */
    fun hideKeyboard(context: Activity?) {
        context?.let {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = context.currentFocus

            if (view == null)
                view = View(context)

            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun removeLastChar(str: String): String {
        return if (str.isEmpty())
            str
        else
            str.substring(0, str.length - 1)
    }

    @JvmStatic
    fun makeBirthday(birthday: String): String {
        return if (TextUtils.isEmpty(birthday) || birthday.length != 8 || !Pattern.matches(
                "(\\d{4})(\\d{2})(\\d{2})",
                birthday
            )
        ) "" else birthday.replace(
            "(\\d{4})(\\d{2})(\\d{2})".toRegex(), "$1.$2.$3"
        )
    }

    /**
     * 0~9의 숫자값을 랜덤한 순서로 생성 후 left, right 배치
     *
     *
     * [    ] [    ] [    ]
     * [    ] [    ] [    ]
     * [    ] [    ] [    ]
     * [left] [    ] [    ]
     *
     * @param left 하단 좌측에 나타낼 항목값
     * @return 생성된 배열
     */
    fun createRandomPad(left: String): Array<String> {
        val defPad =
            ArrayList(ArrayList(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")))
        defPad.shuffle()

        val arrPad = ArrayList(defPad)
        arrPad.add(arrPad.size - 1, left)

        return arrPad.toTypedArray()
    }

    private fun getDateFromPattern(date: String, format: SimpleDateFormat): Date? {
        return try {
            format.parse(date)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Intent 종류에 해당하는 앱이 있는지 확인
     *
     * @param activity 액티비티
     * @param intent   인텐트
     * @return 해당 앱 있음 여부
     */
    private fun checkIntentApp(activity: Activity, intent: Intent): Boolean {
        val packageManager = activity.packageManager
        val activities = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return activities.size > 0
    }

    /**
     * Web Editor Content Screen Fit Size Header
     *
     * @param content HTML
     * @return content with header
     */
    fun getFitScreenHtml(content: String): String {
        return "<html><head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0\">" +
                "<style>img{display: inline;height: auto;max-width: 100%;}</style>" +
                "</head><body>" +
                content +
                "</body></html>"
    }

    fun <T> getSerializableList(serializable: Serializable?, type: Class<T>): ArrayList<T> {
        val arrayList = ArrayList<T>()

        if (serializable is ArrayList<*>) {
            for (obj in serializable) {
                if (type.isInstance(obj)) {
                    val castType = type.cast(obj)

                    castType?.let {
                        arrayList.add(castType)
                    }
                }
            }
        }

        return arrayList
    }

    fun <T> getListSize(arrayList: ArrayList<T>?): Int {
        return arrayList?.size ?: 0
    }

    fun isValidBirthDayPatternYYYYMMDD(date: String): Boolean {
        try {
            val parseDate: Date? = formatYYYYMMDD.parse(date)
            if (parseDate != null) {
                val year = date.substring(0, 4)
                val month = date.substring(4, 6)
                val day = date.substring(6, 8)
                val c = Calendar.getInstance()
                c.time = parseDate
                if (year.toInt() == c[Calendar.YEAR] && month.toInt() == c[Calendar.MONTH] + 1 && day.toInt() == c[Calendar.DATE]
                ) {
                    return true
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "isValidBirthDayPatternYYYYMMDD :: ", e)
        }
        return false
    }

    /**
     * 미성년자 여부 체크
     *
     * @param birthDate 생년월일
     * @param format    생년월일 포맷 형식
     * @return true / false
     */
    fun isAgeMinor(
        birthDate: String,
        format: SimpleDateFormat
    ): Boolean {
        return isAgeYoungerDef(birthDate, format, 19)
    }

    /**
     * 기준값보다 나이가 어린지 확인
     *
     * @param birthDate 생년월일
     * @param format    생년월일 포맷 형식
     * @param defAge    기준 나이
     * @return true / false
     */
    fun isAgeYoungerDef(
        birthDate: String,
        format: SimpleDateFormat,
        defAge: Int
    ): Boolean {
        return calculateAge(birthDate, format) < defAge
    }

    /**
     * 나이 계산
     *
     * @param birthDate 생년월일
     * @param format    생년월일 포맷 형식
     * @return Age
     */
    fun calculateAge(birthDate: String, format: SimpleDateFormat): Int {
        try {
            getDateFromPattern(birthDate, format)?.let{date ->
                val today = Calendar.getInstance()
                val birthday = Calendar.getInstance()

                birthday.time = date
                return today[Calendar.YEAR] - birthday[Calendar.YEAR]

            } ?: return 0

        } catch (e: java.lang.Exception) {
            Log.e(TAG, "calculateAge", e)
        }
        return 0
    }

    private const val patternTelNumber =
        "^\\s*(010|011|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$"
    /**
     * 전화번호 형식 확인
     *
     * @param cellphoneNumber 확인할 전화번호 값
     * @return Pattern Matched
     */
    fun isValidProblemTelNumber(cellphoneNumber: String?): Boolean {
        val p =
            Pattern.compile(patternTelNumber)
        val matcher = p.matcher(cellphoneNumber ?:"")
        return !matcher.matches()
    }

    fun parseSchemeForOutBrowser(activity: Activity?, uri: Uri): Boolean {
        val scheme = uri.scheme
        if (scheme != null && activity != null) {
            when (scheme) {
                "tel" -> {
                    val intent = Intent(Intent.ACTION_DIAL, uri) //콜 다이얼 표시
                    if (checkIntentApp(activity, intent)) activity.startActivity(intent)
                    return false
                }
                "sms" -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.putExtra("address", uri)
                    intent.type = "vnd.android-dir/mms-sms"
                    if (checkIntentApp(activity, intent)) activity.startActivity(intent)
                    return false
                }
                "market" -> {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    return false
                }
                "intent" -> {
                    try {
                        val intent = Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME)
                        intent?.getPackage()?.let { pkg ->
                            val existPackage = activity.packageManager
                                .getLaunchIntentForPackage(pkg)
                            if (existPackage != null) {
                                activity.startActivity(intent)
                            } else {
                                val marketIntent = Intent(Intent.ACTION_VIEW)
                                marketIntent.data =
                                    Uri.parse("market://details?id=" + intent.getPackage())
                                activity.startActivity(marketIntent)
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        Log.e(
                            BuildConfig.APPLICATION_ID,
                            "parseSchemeForOutBrowser :: ",
                            e
                        )
                    }
                    return false
                }
            }
        }
        return true
    }

//    @Suppress("UNCHECKED_CAST")
//    fun <R> readInstanceProperty(instance: Any?, propertyName: String?): R? {
//        return if(instance == null || TextUtils.isEmpty(propertyName))
//            null
//        else {
//            val property = instance::class.memberProperties
//                // don't cast here to <Any, R>, it would succeed silently
//                .first { it.name == propertyName } as KProperty1<Any, *>
//            // force a invalid cast exception if incorrect type here
//            property.get(instance) as R
//        }
//    }
}
