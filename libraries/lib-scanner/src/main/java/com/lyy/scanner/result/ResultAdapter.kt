package com.lyy.scanner.result

import android.animation.Animator
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lyy.impl.SimpleAnimatorListener
import com.lyy.scanner.R
import com.lyy.scanner.databinding.ItemScannerResultBinding
import kotlin.math.abs


/**
 * 扫码结果展示适配器
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-10-09
 */
class ResultAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val mList: ArrayList<String> = ArrayList()

    fun set(list: ArrayList<String>) {

        val sizeBefore = mList.size
        mList.clear()
        mList.addAll(list)
        if (0 == sizeBefore) {
            notifyItemRangeInserted(0, itemCount)
        } else {
            notifyItemRangeChanged(0, itemCount)
        }
    }

    fun add(item: String) {

        mList.add(item)
        notifyItemInserted(itemCount)
    }

    fun del(index: Int) {

        if (index < 0 || index >= itemCount) {
            return
        }
        mList.removeAt(index)
        notifyItemRemoved(index)
    }

    fun clear() {

        val count = itemCount
        mList.clear()
        notifyItemRangeRemoved(0, count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ItemScannerResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(mList[position], object : Callback {
            override fun callback() {
                del(holder.absoluteAdapterPosition)
            }
        })
    }

    override fun getItemCount(): Int {

        return mList.size
    }
}

class ViewHolder(private val binding: ItemScannerResultBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private fun isCollapsed(): Boolean {

        return 0F == binding.content.translationX
    }

    private fun isExpanded(): Boolean {

        val width: Float = binding.delete.width.toFloat()
        return -width == binding.content.translationX
    }
    fun bind(data: String, delCallback: Callback) {

        binding.delete.setOnClickListener {
            // 删除操作
            if (isExpanded()) {
                delCallback.callback()
            }
        }
        binding.content.setOnClickListener {
            // TODO 跳转详情
            if (!isCollapsed()) {
                return@setOnClickListener
            }
            Log.d("[debug]", "TODO 跳转详情")

            val params = Bundle()
            params.putString("raw", data)
            // TODO Router.go(binding.root.context, RouterMap.A_ROUTING_DETAIL, params)
        }

        binding.delete.background.mutate().colorFilter = PorterDuffColorFilter(ContextCompat.getColor(binding.root.context, R.color.colorDelete), PorterDuff.Mode.SRC_IN)
        binding.delete.post {

            val deleteButtonWidth = binding.delete.width.toFloat()
            val touchListener = SwipeTouchListener(deleteButtonWidth)
            binding.content.setOnTouchListener(touchListener)
        }

        binding.result.text = data

        // itemView.setOnClickListener {
        // val clipboardManager: ClipboardManager = itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
        // val clipData = ClipData.newPlainText(null, data)
        // 把数据集设置（复制）到剪贴板
        // clipboardManager.setPrimaryClip(clipData)
        // Toast.makeText(itemView.context, "复制成功", Toast.LENGTH_SHORT).show()
        // }
    }
}

class SwipeTouchListener(private val deleteButtonWidth: Float) : OnTouchListener {

    private var mX: Float? = null
    private var mTranslationX: Float? = null

    private var tsDown: Long = 0L
    private var tsUp: Long = 0L

    private fun translateX(view: View, pTargetTranslationX: Float) {

        val currentTranslationX: Float = view.translationX
        val deltaTranslationX = abs(pTargetTranslationX - currentTranslationX)
        val duration = (deltaTranslationX / deleteButtonWidth * 200).toLong()
        view.animate().translationX(pTargetTranslationX).setDuration(duration)
            .setListener(object : SimpleAnimatorListener() {
                override fun onAnimationEnd(animator: Animator) {
                    view.translationX = pTargetTranslationX
                }
            }).start()
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if (null == view || null == event) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("[debug]", "action down")
                tsDown = System.currentTimeMillis()
                // 记录落脚点
                mX = event.rawX
                mTranslationX = view.translationX
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("[debug]", "action move")
                if (null == mX || null == mTranslationX) {
                    return false
                }
                val deltaX = event.rawX - mX!!
                view.translationX = deltaX + mTranslationX!!
                return false
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                Log.d("[debug]", "action up or cancel")
                if (null == mX || null == mTranslationX) {
                    return false
                }
                tsUp = System.currentTimeMillis()
                if (tsUp - tsDown < 0) {
                    // it should never happen, just avoid ide warning
                    view.performClick()
                }
                val translationX = view.translationX
                val deltaX = event.rawX - mX!!
                if (deltaX < 0F) {
                    // 向左划
                    if (translationX < -deleteButtonWidth / 8f) {
                        // 继续展开
                        translateX(view, -deleteButtonWidth)
                    } else {
                        // 继续闭合
                        translateX(view, 0f)
                    }
                } else {
                    // 向右划
                    if (translationX > -deleteButtonWidth * 7f / 8f) {
                        // 继续闭合
                        translateX(view,0f)
                    } else {
                        // 继续展开
                        translateX(view, -deleteButtonWidth)
                    }
                }
                mX = null
                mTranslationX = null
                return false
            }
            else -> {
                Log.d("[debug]", "action other")
                mX = null
                mTranslationX = null
                return false
            }
        }
    }
}

interface Callback {

    fun callback()
}
