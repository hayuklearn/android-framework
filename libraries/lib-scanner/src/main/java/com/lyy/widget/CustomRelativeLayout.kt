package com.lyy.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * 重写 performClick 方法，规避编译器警告
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-10-09
 */
class
CustomRelativeLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr, 0
    )

    override fun performClick(): Boolean {
        return super.performClick()
    }
}
