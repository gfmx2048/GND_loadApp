package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import timber.log.Timber
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var label = context.getString(R.string.button_name)
    private var rectBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private var rectForegroundColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    private var circeColor = ContextCompat.getColor(context, R.color.colorAccent)
    private var labelColor = ContextCompat.getColor(context, R.color.white)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }
    private val textHeight: Float = paint.descent() - paint.ascent()
    private val textOffset: Float = textHeight / 2 - paint.descent()

    private var radiusOfCircle = 0.0f
    private var startAngleOfCircle = 0 // 0 .. 360
    private var currentSweepAngle = 0 // 0 .. 360
    private var endAngleOfCircle = 360 // 0 .. 360
    private var targetWidth = 0f
    private var rectForArc = RectF(0f, 0f, 0f, 0f)

    private var showCircle = true

    private var valueAnimator = ValueAnimator.ofInt(0, endAngleOfCircle).apply {
        duration = 5000
        interpolator = LinearInterpolator()
        repeatMode = ValueAnimator.REVERSE
        addUpdateListener {
            //angle will increase from 0 to 360
            currentSweepAngle = this.animatedValue as Int

            //width of the foreground rectangle will be increase also
            targetWidth = widthSize.times(this.animatedValue as Int).div(360).toFloat()

            invalidate()
        }
    }

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, old, new ->
        Timber.d("old status $old and new $new")
        when (new) {
            ButtonState.Clicked -> {
               setState(ButtonState.Loading)
            }
            ButtonState.Completed -> {
                label = context.getString(R.string.download)
                showCircle = false
                valueAnimator.cancel()
                invalidate()
            }
            ButtonState.Loading -> {
                label = context.getString(R.string.button_loading)
                paint.measureText(label)
                showCircle = true
                valueAnimator.cancel()
                valueAnimator.start()
            }
        }
    }


    init {
        isClickable = true // enable view to accept user input
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            rectBackgroundColor = getColor(
                R.styleable.LoadingButton_rect_backgroundColor, ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
            rectForegroundColor = getColor(
                R.styleable.LoadingButton_rect_foregroundColor, ContextCompat.getColor(
                    context,
                    R.color.colorPrimaryDark
                )
            )
            circeColor = getColor(
                R.styleable.LoadingButton_circleColor, ContextCompat.getColor(
                    context,
                    R.color.colorAccent
                )
            )
            labelColor = getColor(
                R.styleable.LoadingButton_labelColor, ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
            label = getString(R.styleable.LoadingButton_label) ?: context.getString(R.string.button_name)
        }

        valueAnimator.doOnEnd {
            setState(ButtonState.Completed)
        }
    }

    override fun performClick(): Boolean {
        if(buttonState == ButtonState.Loading) {
            Timber.d("Button is loading, let the animation finish before handle the click")
            return true
        }
        super.performClick()
        setState(ButtonState.Clicked)
        return true
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //Draw button background
        paint.color = rectBackgroundColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        //Draw arc and foreground rect ready for animation
        if (showCircle) {

            paint.color = rectForegroundColor
            canvas?.drawRect(0f, 0f, targetWidth, heightSize.toFloat(), paint)

            paint.color = circeColor
            canvas?.drawArc(
                rectForArc,
                startAngleOfCircle.toFloat(),
                currentSweepAngle.toFloat(),
                true,
                paint
            )
        }

        //Draw text
        paint.color = labelColor
        canvas?.drawText(label, widthSize.div(2).toFloat(), heightSize.div(2).toFloat() + textOffset, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        recalculateValue(w, h)
  }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        recalculateValue(w, h)
        setMeasuredDimension(w, h)
    }


    private fun recalculateValue(w: Int, h: Int) {
        widthSize = w
        heightSize = h
        radiusOfCircle = heightSize.div(2).times(0.5).toFloat()
        rectForArc.set(
            (widthSize.minus(paddingEnd + 60).minus(radiusOfCircle)),
            (heightSize.div(2) - radiusOfCircle),
            (widthSize.minus(paddingEnd + 60).plus(radiusOfCircle)),
            (heightSize.div(2) + radiusOfCircle)
        )
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

}