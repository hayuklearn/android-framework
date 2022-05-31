package com.lyy.scanner.pda

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lyy.scanner.ScannerListener
import com.lyy.scanner.databinding.ActivityPdaScannerBinding
import com.lyy.scanner.phone.ItemDecoration
import com.lyy.scanner.result.ResultAdapter
import com.lyy.scanner.R

/**
 * ChiTeng PDA C65 扫码界面
 *
 * @property binding [@androidx.annotation.NonNull] ActivityScannerBinding
 * @property delegate PDAScannerDelegate
 */
class PDAScannerActivity : AppCompatActivity(), ScannerListener {

    private val binding by lazy { ActivityPdaScannerBinding.inflate(layoutInflater) }

    private lateinit var delegate: PDAScannerDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.loading.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary))
        binding.loading.isEnabled = false

        binding.stub.layoutParams.height = getStatusBarHeight()
        binding.stub.requestLayout()

        binding.layoutScannerResult.list.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.layoutScannerResult.list.addItemDecoration(ItemDecoration(this))
        binding.layoutScannerResult.list.adapter = ResultAdapter()

        binding.layoutScannerOperationPda.clear.setOnClickListener {

            (binding.layoutScannerResult.list.adapter as ResultAdapter).clear()
        }

        delegate = PDAScannerDelegate(this)
    }

    override fun onResume() {
        super.onResume()
        delegate.onScannerResourceInit()
    }

    override fun onPause() {
        delegate.onScannerResourceFreeze()
        super.onPause()
    }

    override fun onDestroy() {
        delegate.onScannerResourceRelease()
        super.onDestroy()
    }

    override fun onScanLoading(loading: Boolean) {

        binding.loading.isRefreshing = loading
    }

    override fun onScanRead(raw: ByteArray?, code: String?) {

        Log.d("PDA", (code ?: "").trim())
        // 扫码结果会带有一个 %0A 的换行符
        (binding.layoutScannerResult.list.adapter as ResultAdapter).add((code ?: "").trim())

        code?.let {
            val params = Bundle()
            params.putString("raw", it.trim())
            // TODO Router.go(this, RouterMap.A_ROUTING_DETAIL, params)
        }
    }

    private fun getStatusBarHeight(): Int {

        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }
}

