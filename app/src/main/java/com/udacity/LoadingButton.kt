package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.udacity.ButtonState.*
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heighSize = 0
    private var progress  = 0f

    private var loadingText:String
    private var loadingTextColor:Int
    private var downloadButtonColor:Int
    private var downloadLoadingColor:Int

    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(Completed) { _, _, new ->
        when(new){
            Loading -> showCircle()
            Completed -> completeAnimation()
        }
    }

    private fun completeAnimation() {
        valueAnimator.cancel()
        progress = 0f
        loadingText = "Download"
        invalidate()
    }

    fun getButtonState(state: ButtonState){
        buttonState = state
    }

    private fun showCircle(){
        valueAnimator = ValueAnimator.ofFloat(0f,1f).apply {
            addUpdateListener {
                progress = animatedValue as Float
                invalidate()
            }
            repeatMode  = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            duration    = 5000
            start()
        }
        loadingText = "We are loading"
        invalidate()
    }

    init {
        isClickable = true
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.LoadingButton,
                0,
                0
        ).apply {
            loadingText = getString(R.styleable.LoadingButton_loadingText).toString()
            loadingTextColor  = getColor(R.styleable.LoadingButton_btnTextColor, 0)
            downloadButtonColor  = getColor(R.styleable.LoadingButton_btnBackground, 0)
            downloadLoadingColor = getColor(R.styleable.LoadingButton_btnLoading, 0)
            recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawColorBtn(canvas)

        if (buttonState == Loading){
            drawLoadingProgress(canvas)
            drawCircle(canvas)
        }
        drawText(canvas)
    }

    private fun drawLoadingProgress(canvas: Canvas){
        val right = progress * widthSize
        val bottom = heighSize.toFloat()
        canvas.drawRect(0f,0f, right, bottom, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = downloadLoadingColor
            style = Paint.Style.FILL
        })
    }

    private fun drawCircle(canvas: Canvas) {
        val size        = 0.5f * heighSize
        val left        = widthSize - (widthSize/3f)
        val top         = heighSize - (0.75f * heighSize)
        val sweepAngle  = progress * 360f
        val circleFrame = RectF(left, top,left + size,top + size)

        canvas.drawArc(circleFrame,0f, sweepAngle, true,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.YELLOW
                    style = Paint.Style.FILL
                })
    }

    private fun drawText(canvas: Canvas) {
        canvas.drawText(loadingText, measuredWidth/2f, heighSize * 0.60f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color     = loadingTextColor
                    textAlign = Paint.Align.CENTER
                    textSize  = 40.0F
                })
    }

    private fun drawColorBtn(canvas: Canvas) {
        canvas.drawRect(0f, heighSize.toFloat(), widthSize.toFloat(),0f,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = downloadButtonColor
                style = Paint.Style.FILL
            })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int    = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int    = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heighSize = h
        setMeasuredDimension(w, h)
    }
}