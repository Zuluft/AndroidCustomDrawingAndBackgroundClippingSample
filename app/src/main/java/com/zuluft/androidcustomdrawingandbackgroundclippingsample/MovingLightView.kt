package com.zuluft.androidcustomdrawingandbackgroundclippingsample

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.doOnPreDraw

class MovingLightView : View {
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            style = Paint.Style.STROKE
            color = Color.parseColor("#808080")
            strokeWidth = 20f
        }
    private val lampPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#FFFF00")
        }

    private val linePath = Path()
    private val lampPath = Path()
    private var lightPath = Path()


    var lineBottomY = -1f
    var lineBottomX = -1f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)

    constructor(context: Context, attrs: AttributeSet?, defStyleArg: Int) : super(
        context,
        attrs,
        defStyleArg
    ) {
        setBackgroundColor(Color.parseColor("#AA000000"))
        doOnPreDraw {
            startAnim()
        }
    }

    private fun startAnim() {
        val arcShape = RectF(0f, 0f, width.toFloat(), getLineBottomYForceInit())
        val path = Path()
            .apply {
                moveTo(0f, arcShape.height() / 2f)
                arcTo(arcShape, 180f, -180f, false)
            }
        val animator = ObjectAnimator.ofFloat(
            this,
            Property.of(javaClass, Float::class.java, "lineBottomX"),
            Property.of(javaClass, Float::class.java, "lineBottomY"),
            path
        ).apply {
            duration = 2000L
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                invalidate()
            }
        }
        animator.start()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLamp(canvas)
    }

    override fun draw(canvas: Canvas) {
        drawLight(canvas)
        super.draw(canvas)
    }

    private fun drawLight(canvas: Canvas) {
        lightPath.reset()
        lightPath.fillType = Path.FillType.INVERSE_WINDING
        lightPath.apply {
            moveTo(
                getLineBottomXForceInit() + getLampCenterOffset(),
                getLineBottomYForceInit() + getLampHeight()
            )
            lineTo(
                getLineBottomXForceInit() - getLampCenterOffset(),
                getLineBottomYForceInit() + getLampHeight()
            )
            //            lineTo(getLineBottomXForceInit() - offset * 2f, Float.MAX_VALUE)
            //            lineTo(getLineBottomXForceInit() + offset * 2f, Float.MAX_VALUE)
            lineTo(-Float.MAX_VALUE, Float.MAX_VALUE)
            lineTo(Float.MAX_VALUE, Float.MAX_VALUE)
            close()
        }
        canvas.clipPath(lightPath)
    }


    private fun getLampHeight(): Float {
        return width * 0.3f
    }

    private fun getLampCenterOffset(): Float {
        return width * 0.1f

    }

    private fun drawLamp(canvas: Canvas) {
        linePath.reset()
        lampPath.reset()
        val lineY = getLineBottomYForceInit()
        val lineX = getLineBottomXForceInit()
        linePath.moveTo(lineX, 0f)
        linePath.lineTo(lineX, lineY)

        lampPath.moveTo(getLineBottomXForceInit(), getLineBottomYForceInit())
        lampPath.lineTo(
            getLineBottomXForceInit() - getLampCenterOffset(),
            getLineBottomYForceInit()
        )
        lampPath.lineTo(
            getLineBottomXForceInit() - getLampCenterOffset() * 2f,
            getLineBottomYForceInit() + getLampHeight()
        )
        lampPath.lineTo(
            getLineBottomXForceInit() + getLampCenterOffset() * 2f,
            getLineBottomYForceInit() + getLampHeight()
        )
        lampPath.lineTo(
            getLineBottomXForceInit() + getLampCenterOffset(),
            getLineBottomYForceInit()
        )
        lampPath.close()
        canvas.drawPath(linePath, linePaint)
        canvas.drawPath(lampPath, lampPaint)
    }

    private fun getLineBottomXForceInit(): Float {
        if (lineBottomX < 0) {
            lineBottomX = width * 0.5f
        }
        return lineBottomX
    }

    private fun getLineBottomYForceInit(): Float {
        if (lineBottomY < 0) {
            lineBottomY = height * 0.3f
        }
        return lineBottomY
    }
}