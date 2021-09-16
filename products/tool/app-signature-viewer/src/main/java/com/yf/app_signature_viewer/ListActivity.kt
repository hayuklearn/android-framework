package com.yf.app_signature_viewer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.af.lib.utils.Android
import kotlinx.coroutines.*
import kotlin.coroutines.suspendCoroutine

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
        val linearLayoutManager = LinearLayoutManager(this);
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRecyclerView.layoutManager = linearLayoutManager
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

        val list = ApkTool.listInstalledPackages(packageManager)
        list.sortBy { it.appName }
        return@withContext list
    }
}

class Adapter : RecyclerView.Adapter<ViewHolder>() {

    private val mList: ArrayList<AppInfo> = ArrayList()

    fun setList(list: ArrayList<AppInfo>) {
        val sizeBefore = mList.size
        mList.clear()
        mList.addAll(list)
        if (0 == sizeBefore) {
            notifyItemRangeInserted(0, itemCount - 1)
        } else {
            notifyItemRangeChanged(0, itemCount - 1)
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

    fun bind(data: AppInfo) {

        itemView.findViewById<AppCompatImageView>(R.id.app_icon).setImageDrawable(data.image)
        itemView.findViewById<AppCompatTextView>(R.id.app_name).text = data.appName
        itemView.findViewById<AppCompatTextView>(R.id.package_name).text = data.packageName

        itemView.setOnClickListener {


            val signature : String = Android.signature(itemView.context, data.packageName)

            val clipboardManager: ClipboardManager =
                itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
            val clipData = ClipData.newPlainText(null, data.appName + "\n" + data.packageName + "\n" + signature)
            // 把数据集设置（复制）到剪贴板
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(itemView.context, "复制签名成功", Toast.LENGTH_SHORT).show();
        }
    }
}
