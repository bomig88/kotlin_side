package bomi.kotlinside.base.ui

import bomi.kotlinside.base.ui.BaseActivity

interface OnActivityEnabledListener {
    fun onActivityEnabled(activity: BaseActivity<*, *>?)
}
