package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    private val pointPosition: PointF = PointF(0.0f,0.0f)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("",Typeface.BOLD)
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        // if ButtonState.Loading -> show "Loading" text and start the animation
        // if ButtonState.Completed -> show "Completed" text and end the animation
        // You can consider other values also here
        // Finally, you will need to redraw the button here. You can call invalidate() for it

        //Set the buttonState of your button to ButtonState.Loading once the button is pressed.
        //Set the buttonState to ButtonState.Completed once the request completes.
    }


    init {

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = Color.GREEN
        canvas?.drawRect(0f,0f,widthSize.toFloat(),heightSize.toFloat(),paint)
        paint.color = Color.YELLOW
        canvas?.drawCircle((widthSize/2).toFloat(),(heightSize/2).toFloat(),40f,paint)
        paint.color = Color.BLACK
        canvas?.drawText("TEst text", 0f,0f,paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthSize = w
        heightSize = h
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
        setMeasuredDimension(w, h)
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

}