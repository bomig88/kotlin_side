package bomi.kotlinside.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import bomi.kotlinside.R

@Suppress("unused")
class UnderLineTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AppCompatTextView(context, attrs, defStyleAttr) {
    private var mRect: Rect? = null
    private lateinit var mPaint: Paint
    private var mStrokeWidth = 0f
    private var isUnderLineEnable = false
    private fun init(
        context: Context,
        attributeSet: AttributeSet?,
        defStyle: Int
    ) {
        val density = context.resources.displayMetrics.density
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.UnderLineTextView, defStyle, 0)
        val underlineColor =
            typedArray.getColor(R.styleable.UnderLineTextView_underlineColor, -0x10000)
        mStrokeWidth =
            typedArray.getDimension(R.styleable.UnderLineTextView_underlineWidth, density * 2)
        isUnderLineEnable =
            typedArray.getBoolean(R.styleable.UnderLineTextView_underlineEnable, true)
        typedArray.recycle()

        mRect = Rect()
        mPaint = Paint()
        mPaint.style = Paint.Style.STROKE
        mPaint.color = underlineColor
        mPaint.strokeWidth = mStrokeWidth
    }

    fun setUnderLineEnable(isUnderLineEnable: Boolean) {
        this.isUnderLineEnable = isUnderLineEnable
        setPadding(
            paddingLeft,
            paddingTop,
            paddingRight,
            (paddingBottom + mStrokeWidth).toInt()
        )
    }

    var underLineColor: Int
        get() = mPaint.color
        set(mColor) {
            mPaint.color = mColor
            invalidate()
        }

    var underlineWidth: Float
        get() = mStrokeWidth
        set(mStrokeWidth) {
            this.mStrokeWidth = mStrokeWidth
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        if (isUnderLineEnable) {
            val count = lineCount
            val layout = layout
            var xStart: Float
            var xStop: Float
            var xDiff: Float
            var firstCharInLine: Int
            var lastCharInLine: Int
            for (i in 0 until count) {
                val baseline = getLineBounds(i, mRect)
                firstCharInLine = layout.getLineStart(i)
                lastCharInLine = layout.getLineEnd(i)
                xStart = layout.getPrimaryHorizontal(firstCharInLine)
                xDiff = layout.getPrimaryHorizontal(firstCharInLine) - xStart
                xStop = layout.getPrimaryHorizontal(lastCharInLine) + xDiff
                canvas.drawLine(
                    xStart,
                    baseline + mStrokeWidth + mStrokeWidth + mStrokeWidth,
                    xStop,
                    baseline + mStrokeWidth + mStrokeWidth + mStrokeWidth,
                    mPaint
                )
            }
        }
        super.onDraw(canvas)
    }

    init {
        init(context, attrs, defStyleAttr)
    }
}