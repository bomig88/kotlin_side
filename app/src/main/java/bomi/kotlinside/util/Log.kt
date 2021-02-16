package bomi.kotlinside.util

import bomi.kotlinside.BuildConfig

/**
 * Release 대비 로그 관리 유틸
 */
@Suppress("unused","MemberVisibilityCanBePrivate")
object Log {
    private const val CUTTING_LENGTH = 2500

    fun d(tag: String, msg: String?) {
        if (BuildConfig.DEBUG) {
            if (msg != null && msg.length > CUTTING_LENGTH) {
                android.util.Log.d(tag, msg.substring(0, CUTTING_LENGTH))
                d(tag, msg.substring(CUTTING_LENGTH))
            } else
                android.util.Log.d(tag, msg ?:"")
        }
    }

    fun e(tag: String, msg: String?) {
        if (BuildConfig.DEBUG) {
            if (msg != null && msg.length > CUTTING_LENGTH) {
                android.util.Log.e(tag, msg.substring(0, CUTTING_LENGTH))
                e(tag, msg.substring(CUTTING_LENGTH))
            } else
                android.util.Log.e(tag, msg ?:"")
        }
    }

    fun e(tag: String, msg: String?, e: Exception) {
        if (BuildConfig.DEBUG) {
            if (msg != null && msg.length > CUTTING_LENGTH) {
                android.util.Log.d(tag, msg.substring(0, CUTTING_LENGTH), e)
                e(tag, msg.substring(CUTTING_LENGTH), e)
            } else
                android.util.Log.d(tag, msg, e)
        }
    }

    fun w(tag: String, msg: String?) {
        if (BuildConfig.DEBUG) {
            if (msg != null && msg.length > CUTTING_LENGTH) {
                android.util.Log.w(tag, msg.substring(0, CUTTING_LENGTH))
                w(tag, msg.substring(CUTTING_LENGTH))
            } else
                android.util.Log.w(tag, msg ?:"")
        }
    }

    fun v(tag: String, msg: String?) {
        if (BuildConfig.DEBUG) {
            if (msg != null && msg.length > CUTTING_LENGTH) {
                android.util.Log.v(tag, msg.substring(0, CUTTING_LENGTH))
                v(tag, msg.substring(CUTTING_LENGTH))
            } else
                android.util.Log.v(tag, msg ?:"")
        }
    }

    fun i(tag: String, msg: String?) {
        if (BuildConfig.DEBUG) {
            if (msg != null && msg.length > CUTTING_LENGTH) {
                android.util.Log.i(tag, msg.substring(0, CUTTING_LENGTH))
                i(tag, msg.substring(CUTTING_LENGTH))
            } else
                android.util.Log.i(tag, msg ?:"")
        }
    }
}