package com.lyy.scanner.phone

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.lyy.scanner.ScannerListener
import com.lyy.scanner.databinding.ActivityPhoneScannerBinding
import com.lyy.scanner.result.ResultAdapter
import com.lyy.scanner.R


/**
 * 手机扫码界面
 *
 * @property binding [@androidx.annotation.NonNull] ActivityScannerBinding
 * @property delegate PDAScannerDelegate
 */
class PhoneScannerActivity : AppCompatActivity(),
    //
    ScannerListener {

    private val binding by lazy { ActivityPhoneScannerBinding.inflate(layoutInflater) }

    private val lightViewModel by viewModels<LightViewModel>()

    private lateinit var delegate: PhoneScannerDelegate

    private val animation: TranslateAnimation = TranslateAnimation(
        //
        Animation.RELATIVE_TO_PARENT, 0.0f,
        //
        Animation.RELATIVE_TO_PARENT, 0.0f,
        //
        Animation.RELATIVE_TO_PARENT, -1.0f,
        //
        Animation.RELATIVE_TO_PARENT, 0.0f
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(binding.root)

        binding.loading.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary))
        binding.loading.isEnabled = false

        binding.layoutScannerResult.list.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.layoutScannerResult.list.addItemDecoration(ItemDecoration(this))
        binding.layoutScannerResult.list.adapter = ResultAdapter()
        binding.layoutScannerOperationPhone.clear.setOnClickListener {

            (binding.layoutScannerResult.list.adapter as ResultAdapter).clear()
        }
        binding.layoutScannerOperationPhone.scan.background.mutate().colorFilter =
            PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorScan), PorterDuff.Mode.SRC_IN)
        binding.layoutScannerOperationPhone.scan.setOnClickListener {

            delegate.restartPreviewAfterDelay()
        }
        binding.layoutScannerOperationPhone.light.setOnClickListener {
            lightViewModel.toggle()
        }

        delegate = PhoneScannerDelegate(this, binding, this)

        val statusBarHeight = getStatusBarHeight()
        // 状态栏占位
        binding.layoutScannerPhone.stubStatusBar.layoutParams.height = statusBarHeight
        binding.layoutScannerPhone.stubStatusBar.requestLayout()

        // 扫描区域占位
        // 360DP 的基准屏幕宽度，高度定为 270DP，对应 3/4 屏宽，减去上下各 20DP PADDING 和 STATUS BAR 高度
        binding.layoutScannerPhone.crop.layoutParams.height = dp2px(270F - 40F) - statusBarHeight
        binding.layoutScannerPhone.crop.requestLayout()

        lightViewModel.on.observe(this) {
            delegate.light(it)
            binding.layoutScannerOperationPhone.light.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, if (it) {
                    R.drawable.ic_light_on
                } else {
                    R.drawable.ic_light_off
                }, 0, 0
            )
            binding.layoutScannerOperationPhone.light.text = if (it) {
                "LIGHT ON"
            } else {
                "LIGHT OFF"
            }
            binding.layoutScannerOperationPhone.light.setTextColor(
                if (it) {
                    ContextCompat.getColor(this, R.color.colorLightOn)
                } else {
                    ContextCompat.getColor(this, R.color.colorLightOff)
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        delegate.onScannerResourceInit()
        lightViewModel.off()
    }

    override fun onPause() {
        lightViewModel.off()
        delegate.onScannerResourceFreeze()
        super.onPause()
    }

    override fun onDestroy() {
        delegate.onScannerResourceRelease()
        super.onDestroy()
    }

    override fun onScanLoading(loading: Boolean) {

        if (loading) {
            startAnimation()
        } else {
            stopAnimation()
        }
        // binding.loading.isRefreshing = loading
    }

    override fun onScanRead(raw: ByteArray?, code: String?) {

        (binding.layoutScannerResult.list.adapter as ResultAdapter).add(code ?: "")

        code?.let {
            Log.d("code", it)
            val params = Bundle()
            params.putString("raw", it)
            // TODO Router.go(this, RouterMap.A_ROUTING_DETAIL, params)
        }
        raw?.let {
            // val bitmap = Util.bytes2Bitmap(it)
            // LogService.logNormal("[debug]", "竖屏角度 - bitmap w=${bitmap.width}, h=${bitmap.height}")
            // binding.layoutScannerResult.image.setImageBitmap(bitmap)
        }
    }

    /**
     * 扫描动画
     */
    private fun startAnimation() {

        animation.duration = 2000
        animation.repeatCount = -1
        animation.repeatMode = Animation.RESTART
        binding.layoutScannerPhone.cropLine.startAnimation(animation)
    }

    private fun stopAnimation() {

        binding.layoutScannerPhone.cropLine.clearAnimation()
        binding.layoutScannerPhone.cropLine.visibility = View.GONE
    }

    private fun getStatusBarHeight(): Int {

        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    private fun dp2px(dp: Float) = (dp * resources.displayMetrics.density + 0.5f).toInt()
}
