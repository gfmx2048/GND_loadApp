package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var label = context.getString(R.string.button_name)
    private var rectBackgroundColor = ContextCompat.getColor(context,R.color.colorPrimary)
    private var rectForegroundColor = ContextCompat.getColor(context,R.color.colorPrimaryDark)
    private var circeColor = ContextCompat.getColor(context,R.color.colorAccent)
    private var labelColor = ContextCompat.getColor(context,R.color.colorBlack)
    private var radiusOfCircle = 0.0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("",Typeface.BOLD)
    }

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        // if ButtonState.Loading -> show "Loading" text and start the animation
        // if ButtonState.Completed -> show "Completed" text and end the animation
        // You can consider other values also here
        // Finally, you will need to redraw the button here. You can call invalidate() for it

        //Set the buttonState of your button to ButtonState.Loading once the button is pressed.
        //Set the buttonState to ButtonState.Completed once the request completes.
    }


    init {
        isClickable = true // enable view to accept user input
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            rectBackgroundColor = getColor(R.styleable.LoadingButton_rect_backgroundColor, ContextCompat.getColor(context,R.color.colorPrimary))
            rectForegroundColor = getColor(R.styleable.LoadingButton_rect_foregroundColor, ContextCompat.getColor(context,R.color.colorPrimaryDark))
            circeColor = getColor(R.styleable.LoadingButton_circleColor, ContextCompat.getColor(context,R.color.colorAccent))
            labelColor = getColor(R.styleable.LoadingButton_labelColor, ContextCompat.getColor(context,R.color.colorBlack))
            label = getString(R.styleable.LoadingButton_label)?:context.getString(R.string.button_name)
        }
    }

    override fun performClick(): Boolean {
        if(super.performClick()) return true
        label = context.getString(R.string.button_loading)
        invalidate()
        return true
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = rectBackgroundColor
        canvas?.drawRect(0f,0f,widthSize.toFloat(),heightSize.toFloat(),paint)
        paint.color = rectForegroundColor
        canvas?.drawRect(0f,0f,widthSize.div(3).toFloat(),heightSize.toFloat(),paint)
        paint.color = circeColor
        canvas?.drawCircle(widthSize.div(2).toFloat(),heightSize.div(2).toFloat(),radiusOfCircle,paint)
        paint.color = labelColor
        canvas?.drawText(label, 80f ,heightSize.div(2).toFloat(),paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthSize = w
        heightSize = h
        radiusOfCircle = heightSize.div(2).times(0.5).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        radiusOfCircle = heightSize.div(2).toFloat()
        setMeasuredDimension(w, h)
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

}