package com.af.app_view.moon_view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.af.app_view.R

/**
 * 月亮动画
 *
 * @author https://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650839547&idx=1&sn=93d9707bfe8e33649b0e44c919a84e42&chksm=80b74d65b7c0c4739ab3913af6d234f8a840382e357c1c6d8180fc38ae34475ff843a5865ed6&mpshare=1&scene=24&srcid=0922WQ3qqxYTS1KlLBgWolsr&sharer_sharetime=1632245145692&sharer_shareid=08809ea9b2f08ade28312e91e0c7a024&ascene=14&devicetype=iOS14.8&version=18000d31&nettype=WIFI&abtest_cookie=AAACAA%3D%3D&lang=zh_CN&fontScale=94&exportkey=AfCyApFxr0xzPWKf%2BlYEav8%3D&pass_ticket=9g1c1euSOiAQ%2BPXOBxeNYVUHQK%2BsfdzSAH9ejaPX0dhH69nBrXpD9aGaSiQmrQ0B&wx_header=1
 * @date 2021-09-23
 */
class MoonView(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    View(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr, 0
    )

    // ---------------------------------------------------------------------------------------------

     var mRotate: Int = 0
        set(value) {
            field = value
            invalidate()
        }
     var mPhase: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    private val paint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    init {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.MoonView, defStyleAttr, 0
        )
        a.recycle()
    }
    // ---------------------------------------------------------------------------------------------

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        // TODO
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let {
            // 黑色背景上面画了个橙色的正圆
            it.drawColor(ContextCompat.getColor(context!!, android.R.color.black))
            val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            paint.color = ContextCompat.getColor(context!!, android.R.color.holo_orange_light)
            val radius = Math.min(width, height) * 0.3f
            it.drawCircle(rectF.centerX(), rectF.centerY(), radius, paint)

            val c = it.saveLayer(
                RectF(0f, 0f, width.toFloat(), height.toFloat()),
                null
            )
            paint.isDither = true
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)

            // 下面的这些计算跟mPhase的改变方式有关
            // 首先mPhase 是由CountDownTimer进行修改的
            // 创建一个矩形，固定中心在屏幕中间
            val rectFOval = when {
                mPhase > 150 -> {
                    // 这里椭圆的 `Minor axis` 在变小   眉月 -> 上弦月
                    RectF(
                        rectF.centerX() - radius * (mPhase - 150) / 150,
                        rectF.centerY() - radius,
                        rectF.centerX() + radius * (mPhase - 150) / 150,
                        rectF.centerY() + radius
                    )
                }
                mPhase < 150 -> {
                    // 这里椭圆的 `Minor axis` 在变大。上弦月 -> 盈凸月
                    RectF(
                        rectF.centerX() - (radius - radius * mPhase / 150),
                        rectF.centerY() - radius,
                        rectF.centerX() + (radius - radius * mPhase / 150),
                        rectF.centerY() + radius
                    )
                }
                else -> {
                    null
                }
            }

            val rectFCircle = RectF(
                rectF.centerX() - radius,
                rectF.centerY() - radius,
                rectF.centerX() + radius,
                rectF.centerY() + radius
            )

            paint.color = ContextCompat.getColor(context!!, android.R.color.black)

            when {
                mPhase == 150 -> {
                    if (rectFOval != null) {
                        it.drawOval(rectFOval, paint)
                    }
                    it.drawArc(rectFCircle, 90f, 180f, false, paint)
                }
                mPhase == 0 -> {
                }
                mPhase < 150 -> {
                    // 先画半圆，再画椭圆
                    it.drawArc(rectFCircle, 90f, 180f, false, paint)
                    // 当 'Minor axis' 的长度减少 0, 然后再增加。月相的变化是 眉月 -> 上弦月 -> 盈凸月
                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
                    it.drawOval(rectFOval!!, paint)
                }
                else -> {
                    it.drawOval(rectFOval!!, paint)
                    it.drawArc(rectFCircle, 90f, 180f, false, paint)
                }
            }
            paint.xfermode = null
            it.restoreToCount(c)
            Log.d("LunarPhase", "mRotate=$mRotate")
            // Rotate the canvas. For recording preview.
            it.rotate(mRotate.toFloat())
        }
    }
}
