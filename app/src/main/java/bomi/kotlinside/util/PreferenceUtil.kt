package bomi.kotlinside.util

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import bomi.kotlinside.MainApplication
import java.util.*

@Suppress("unused")
class PreferenceUtil(context: Context) {
    private val mPref: SharedPreferences
    private val mEditor: SharedPreferences.Editor

    val uuid: String
        get() {
            val uuid = getString(PREF_KEY_UUID, "")

            return if (TextUtils.isEmpty(uuid)) {
                createUUID()
                getString(PREF_KEY_UUID, "")
            } else
                uuid
        }

    var flagFirstAppStart: Boolean
        get() = getBoolean(PREF_KEY_FLAG_FIRST_APP_START, true)
        set(flag) = put(PREF_KEY_FLAG_FIRST_APP_START, flag)

    var flagFirstAgreementStart: Boolean
        get() = getBoolean(PREF_KEY_FLAG_FIRST_AGREEMENT_START, true)
        set(flag) = put(PREF_KEY_FLAG_FIRST_AGREEMENT_START, flag)

    var flagPinCodeInit: Boolean
        get() = getBoolean(PREF_KEY_FLAG_PIN_CODE_INIT, true)
        set(value) = put(PREF_KEY_FLAG_PIN_CODE_INIT, value)

    var pinCode: String
        get() = getString(PREF_KEY_PIN_CDE, "")
        set(value) = put(PREF_KEY_PIN_CDE, value)

    var flagHaveSaveReport: Boolean
        get() = getBoolean(PREF_KEY_FLAG_HAVE_SAVE_REPORT, false)
        set(flag) = put(PREF_KEY_FLAG_HAVE_SAVE_REPORT, flag)

    var jsonNotSeeToday: String
        get() = getString(PREF_POPUP_NOT_SEE_TODAY, "{}")
        set(value) = put(PREF_POPUP_NOT_SEE_TODAY, value)

    var jsonNotSeeAfter: String
        get() = getString(PREF_POPUP_NOT_SEE_AFTER, "{}")
        set(value) = put(PREF_POPUP_NOT_SEE_AFTER, value)

    init {
        mPref = context.getSharedPreferences(APP_PREFERENCE_NAME, Context.MODE_PRIVATE)
        mEditor = mPref.edit()
        mEditor.apply()
    }

    private fun createUUID() {
        put(PREF_KEY_UUID, "AND_" + UUID.randomUUID() + "_" + System.currentTimeMillis())
    }

    private fun put(key: String, value: String?) {
        mEditor.putString(key, value)
        mEditor.apply()
    }

    private fun put(key: String, value: Int) {
        mEditor.putInt(key, value)
        mEditor.apply()
    }

    private fun put(key: String, value: Long) {
        mEditor.putLong(key, value)
        mEditor.apply()
    }

    private fun put(key: String, value: Boolean) {
        mEditor.putBoolean(key, value)
        mEditor.apply()
    }

    private fun put(key: String, value: Float) {
        mEditor.putFloat(key, value)
        mEditor.apply()
    }

    fun remove(key: String) {
        mEditor.remove(key)
        mEditor.apply()
    }

    fun getString(key: String, def: String): String {
        return if (mPref.contains(key)) {
            val searchValue = mPref.getString(key, def)
            if (TextUtils.isEmpty(searchValue)) {
                def
            } else {
                searchValue ?:""
            }
        } else
            def
    }

    private fun getInt(key: String, def: Int): Int {
        return if (mPref.contains(key))
            mPref.getInt(key, def)
        else
            def
    }

    private fun getLong(key: String, def: Long): Long {
        return if (mPref.contains(key))
            mPref.getLong(key, def)
        else
            def
    }

    private fun getFloat(key: String, def: Float): Float {
        return if (mPref.contains(key))
            mPref.getFloat(key, def)
        else
            def
    }

    private fun getBoolean(key: String, def: Boolean): Boolean {
        return if (mPref.contains(key))
            mPref.getBoolean(key, def)
        else
            def
    }

    fun clear() {
        mEditor.clear()
        mEditor.apply()
    }

    companion object {
        private const val APP_PREFERENCE_NAME = "APP_PREFERENCE_NAME" + "_" + "BOMI_KOTLIN_SIDE"

        private const val PREF_KEY_UUID = "PREF_KEY_UUID"

        private const val PREF_KEY_FLAG_FIRST_APP_START = "PREF_KEY_FLAG_FIRST_APP_START"
        private const val PREF_KEY_FLAG_FIRST_AGREEMENT_START = "PREF_KEY_FLAG_FIRST_AGREEMENT_START"
        private const val PREF_KEY_FLAG_PIN_CODE_INIT = "PREF_KEY_FLAG_PIN_CODE_INIT"

        private const val PREF_KEY_FLAG_HAVE_SAVE_REPORT = "PREF_KEY_FLAG_HAVE_SAVE_REPORT"
        private const val PREF_KEY_PIN_CDE = "PREF_KEY_PIN_CODE"

        private const val PREF_POPUP_NOT_SEE_TODAY = "PREF_POPUP_NOT_SEE_TODAY"
        private const val PREF_POPUP_NOT_SEE_AFTER = "PREF_POPUP_NOT_SEE_AFTER"
    }
}
