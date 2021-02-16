package bomi.kotlinside.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import androidx.annotation.StringRes
import bomi.kotlinside.MainApplication

/**
 * 알림 팝업용 파라미터 클래스
 */
class DAlertParam internal constructor() {
    var mMessage: CharSequence? = null
    var mMessageSpanned: Spanned? = null
    var mPositiveButtonText: CharSequence? = null
    var mNegativeButtonText: CharSequence? = null
    var mCancelable: Boolean = false
    var mPositiveButtonListener: DialogInterface.OnClickListener? = null
    var mNegativeButtonListener: DialogInterface.OnClickListener? = null
    var mOnCancelListener: DialogInterface.OnCancelListener? = null
    var mOnDismissListener: DialogInterface.OnDismissListener? = null

    fun convertAlertDialogBuilder(context: Context) : AlertDialog.Builder {
        val dialogBuilder = AlertDialog.Builder(context)

        if(mMessage != null)
            dialogBuilder.setMessage(mMessage)

        if(mMessageSpanned != null)
            dialogBuilder.setMessage(mMessageSpanned)

        if(mPositiveButtonText != null)
            dialogBuilder.setPositiveButton(mPositiveButtonText, mPositiveButtonListener)

        if(mNegativeButtonText != null)
            dialogBuilder.setNegativeButton(mNegativeButtonText, mNegativeButtonListener)

        if(mOnCancelListener != null)
            dialogBuilder.setOnCancelListener(mOnCancelListener)

        if(mOnDismissListener != null)
            dialogBuilder.setOnDismissListener(mOnDismissListener)

        dialogBuilder.setCancelable(mCancelable)

        return dialogBuilder
    }

    @Suppress("unused")
    class Builder {
        private val dAlertParam: DAlertParam = DAlertParam()

        fun setMessage(@StringRes messageId: Int): Builder {
            dAlertParam.mMessage = MainApplication.instance.getText(messageId)
            return this
        }

        fun setMessage(message: CharSequence): Builder {
            dAlertParam.mMessage = message
            return this
        }

        fun setSpannedMessage(@StringRes spannedMessageId: Int): Builder {
            dAlertParam.mMessageSpanned =
                getHtmlTagString(MainApplication.instance.getString(spannedMessageId).replace("\n", "<br>"))

            return this
        }

        fun setSpannedMessage(message: String): Builder {
            dAlertParam.mMessageSpanned = getHtmlTagString(message)

            return this
        }

        fun setPositiveButton(@StringRes textId: Int, listener: DialogInterface.OnClickListener?): Builder {
            dAlertParam.mPositiveButtonText = MainApplication.instance.getText(textId)
            dAlertParam.mPositiveButtonListener = listener
            return this
        }

        fun setPositiveButton(text: CharSequence, listener: DialogInterface.OnClickListener?): Builder {
            dAlertParam.mPositiveButtonText = text
            dAlertParam.mPositiveButtonListener = listener
            return this
        }

        fun setNegativeButton(@StringRes textId: Int, listener: DialogInterface.OnClickListener?): Builder {
            dAlertParam.mNegativeButtonText = MainApplication.instance.getText(textId)
            dAlertParam.mNegativeButtonListener = listener
            return this
        }

        fun setNegativeButton(text: CharSequence, listener: DialogInterface.OnClickListener?): Builder {
            dAlertParam.mNegativeButtonText = text
            dAlertParam.mNegativeButtonListener = listener
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            dAlertParam.mCancelable = cancelable
            return this
        }

        fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener?): Builder {
            dAlertParam.mOnCancelListener = onCancelListener
            return this
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener?): Builder {
            dAlertParam.mOnDismissListener = onDismissListener
            return this
        }

        /**
         * Creates an [AlertDialog] with the arguments supplied to this
         * builder.
         *
         *
         * Calling this method does not display the dialog. If no additional
         * processing is needed, [.show] may be called instead to both
         * create and display the dialog.
         */
        fun create(): DAlertParam {
            return dAlertParam
        }

        @Suppress("DEPRECATION")
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

    }
}
