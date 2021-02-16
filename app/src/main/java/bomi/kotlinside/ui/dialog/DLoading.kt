package bomi.kotlinside.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import bomi.kotlinside.R
import bomi.kotlinside.databinding.DialogProgressBinding

/**
 * 네트워크 로딩 중 띄우는 팝업
 */
class DLoading(context: Context) : Dialog(context) {
    private lateinit var binder: DialogProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)

        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        binder = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_progress,
            null,
            false
        )
        setContentView(binder.root)

        val loadingImage = binder.ivLogo.background
        if (loadingImage is Animatable) {
            loadingImage.start()
        }
    }
}
