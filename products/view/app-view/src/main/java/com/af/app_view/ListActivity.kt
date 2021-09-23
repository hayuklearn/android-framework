package com.af.app_view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.af.lib.utils.Android
import kotlinx.coroutines.*

/**
 * Created by hayukleung@gmail.com on 2021-09-16.
 */
class ListActivity : AppCompatActivity() {

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        mSwipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
        mRecyclerView = findViewById(R.id.recycler_view)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.addItemDecoration(ItemDecoration(this))
        mRecyclerView.adapter = Adapter()
    }

    override fun onResume() {

        super.onResume()
        refresh()
    }

    private fun refresh() {

        CoroutineScope(Dispatchers.Main).launch {

            mSwipeRefreshLayout.isRefreshing = true
            val list = run()
            (mRecyclerView.adapter as Adapter).setList(list)
            mSwipeRefreshLayout.isRefreshing = false
        }
    }

    private suspend fun run() = withContext(Dispatchers.IO) {

        val list = ArrayList<ViewInfo>()
        list.add(ViewInfo("MonthView"))
        list.sortBy { it.viewName }
        return@withContext list
    }
}

class Adapter : RecyclerView.Adapter<ViewHolder>() {

    private val mList: ArrayList<ViewInfo> = ArrayList()

    fun setList(list: ArrayList<ViewInfo>) {
        val sizeBefore = mList.size
        mList.clear()
        mList.addAll(list)
        if (0 == sizeBefore) {
            notifyItemRangeInserted(0, itemCount)
        } else {
            notifyItemRangeChanged(0, itemCount)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {

        return mList.size
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(data: ViewInfo) {

        itemView.findViewById<AppCompatTextView>(R.id.view_name).text = data.viewName

        itemView.setOnClickListener {
            // TODO
        }
    }
}

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
        mPadding = Android.dp2px(context, 0F)
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
        outRect.left = mPadding
        outRect.right = mPadding
        outRect.top = mPadding
        outRect.bottom = mPadding
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
            val top = child.bottom + mPadding
            val right = child.right
            val bottom = top + mLine!!.intrinsicHeight
            mLine.setBounds(left, top, right, bottom)
            mLine.draw(c)
        }
    }
}

