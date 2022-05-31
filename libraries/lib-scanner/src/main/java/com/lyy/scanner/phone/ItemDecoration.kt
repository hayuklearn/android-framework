package com.lyy.scanner.phone

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import com.lyy.scanner.R

/**
 *
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-10-20
 */
class ItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val mLine: Drawable?

    // private val bitmap: Bitmap
    private val mPaint: Paint

    private val mPadding: Int

    init {
        val attrs = intArrayOf(android.R.attr.listDivider)
        val a = context.obtainStyledAttributes(attrs)
        mLine = a.getDrawable(0)
        a.recycle()
        mPaint = Paint()
        // bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
        // mPadding = Android.dp2px(context, 12F)

        mPadding = getPXFromDimenResId(context, R.dimen.common_padding_x1)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        drawHorizontal(c, parent, state)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        // val childCount: Int = parent.childCount
        // for (i in 0 until childCount) {
        // if (i == 0) {
        // outRect.top = mPadding
        // }
        // }
        outRect.left = mPadding
        outRect.right = mPadding
        outRect.top = mPadding
        // outRect.bottom = mPadding
    }

    /**
     * 画水平分割线
     */
    private fun drawHorizontal(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val childCount: Int = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val left = child.left
            val top = child.bottom
            val right = child.right
            val bottom = top + mLine!!.intrinsicHeight
            mLine.setBounds(left, top, right, bottom)
            mLine.draw(c)
        }
    }

    private fun getPXFromDimenResId(context: Context, @DimenRes resId: Int): Int {

        return dp2px(context, getXMLValue(context, resId).toFloat())
    }

    private fun dp2px(context: Context, dp: Float): Int {

        return (dp * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    private fun getXMLValue(context: Context, @DimenRes resId: Int): Int {

        val typedValue = TypedValue()
        context.resources.getValue(resId, typedValue, true)
        return TypedValue.complexToFloat(typedValue.data).toInt()
    }
}
