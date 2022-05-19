package com.lyy.ev.operation.widget.loading

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * =================================================================================================
 *     __
 *    / /  ___    __  __  ____ _  ____    __  __  ____ _  ____
 *   / /  / _ \  / / / / / __ `/ / __ \  / / / / / __ `/ / __ \
 *  / /  /  __/ / /_/ / / /_/ / / /_/ / / /_/ / / /_/ / / /_/ /
 * /_/   \___/  \__, /  \__,_/  \____/  \__, /  \__,_/  \____/
 *             /____/                  /____/
 * =================================================================================================
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-04-25
 */
class LoadingFrameLayout(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes), Loadingable {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr, 0
    ) {

        this.delegate.init(attrs)
    }

    //

    private val delegate by lazy { LoadingDelegate(this) }

    override fun isLoading() = this.delegate.isRefreshing()

    override fun showLoading() {

        this.delegate.setRefreshing(true)
    }

    override fun hideLoading() {

        this.delegate.setRefreshing(false)
    }

    override fun setProgressViewEndTarget(scale: Boolean, end: Int) {
        this.delegate.setProgressViewEndTarget(scale, end)
    }

    //

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        this.delegate.onMeasure()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        this.delegate.onLayout()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
    }

    override fun setChildrenDrawingOrderEnabledPublic(enabled: Boolean) {
        super.setChildrenDrawingOrderEnabled(enabled)
    }

    override fun getChildDrawingOrder(childCount: Int, drawingPosition: Int): Int {
        return this.delegate.getChildDrawingOrder(childCount, drawingPosition)
    }
}