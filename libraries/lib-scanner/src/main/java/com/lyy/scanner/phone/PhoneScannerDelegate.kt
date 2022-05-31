package com.lyy.scanner.phone

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.zxing.BinaryBitmap
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.lyy.scanner.IScannerLifecycle
import com.lyy.scanner.ScannerListener
import com.lyy.scanner.databinding.ActivityPhoneScannerBinding
import com.permissionx.guolindev.PermissionX
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 手机端扫码代理
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-09-30
 */
class PhoneScannerDelegate(
    private val phoneScannerActivity: PhoneScannerActivity,
    private val binding: ActivityPhoneScannerBinding,
    private val scannerListener: ScannerListener
) : IScannerLifecycle {

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraExecutor: ExecutorService? = null
    private var camera: Camera? = null
    private var luminosityAnalyzer: LuminosityAnalyzer? = null

    companion object {

        const val TAG = "PhoneScannerDelegate"
    }

    override fun onScannerResourceInit() {
        Log.d(TAG, "onScannerResourceInit")
        cameraExecutor = Executors.newSingleThreadExecutor()
        requestCamera()
    }

    override fun onScanStart() {
        Log.d(TAG, "onScanStart")
        scannerListener.onScanLoading(true)
    }

    override fun onScanStop() {
        Log.d(TAG, "onScanStop")
        scannerListener.onScanLoading(false)
    }

    override fun onScanTimeout() {
        Log.d(TAG, "onScanTimeout")
        scannerListener.onScanLoading(false)
    }

    override fun onScannerResourceFreeze() {
        Log.d(TAG, "onScannerResourceFreeze")
        light(false)
        cameraExecutor?.shutdown()
        cameraExecutor = null
    }

    override fun onScannerResourceRelease() {
        Log.d(TAG, "onScannerResourceRelease")
    }

    // ---------------------------------------------------------------------------------------------

    private fun requestCamera() {

        PermissionX.init(phoneScannerActivity).permissions(Manifest.permission.CAMERA)
            .explainReasonBeforeRequest()
            .request { allGranted, _, _ ->
                if (allGranted) {
                    initCameraX(binding.layoutScannerPhone.preview)
                }
            }
    }

    private fun initCameraX(previewView: PreviewView) {

        cameraProviderFuture = ProcessCameraProvider.getInstance(phoneScannerActivity)
        cameraProviderFuture?.addListener({

            val previewW = previewView.width
            val previewH = previewView.height
            // 获取使用的屏幕比例分辨率属性
            val ratio = Util.aspectRatio(previewW, previewH)
            // val size = Util.calculateSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
            // val size = Util.calculateSize(displayMetrics.widthPixels, displayMetrics.heightPixels)

            // 摄像头
            cameraProvider = cameraProviderFuture?.get()
            // 解析
            val imageAnalyzer = ImageAnalysis.Builder()
                // .setTargetResolution(size)
                .setTargetAspectRatio(ratio)
                .build().also { imageAnalysis ->
                    cameraExecutor?.let {
                        val statusBarHeight = getStatusBarHeight(phoneScannerActivity)
                        val statusBarHeightRelative =
                            statusBarHeight.toFloat() / getScreenWidth(phoneScannerActivity)
                                .toFloat()
                        luminosityAnalyzer = LuminosityAnalyzer(object : IScanResult {

                            override fun invoke(raw: ByteArray, code: String) {

                                Log.d("[解析结果]", code)

                                cameraProviderFuture?.addListener({
                                    // cameraProvider?.unbind(imageAnalysis)
                                    luminosityAnalyzer?.let { analyzer ->
                                        onScanStop()
                                        analyzer.pause()
                                    }
                                    scannerListener.onScanRead(raw, code)
                                }, ContextCompat.getMainExecutor(phoneScannerActivity))
                            }
                        }, statusBarHeightRelative)
                        imageAnalysis.setAnalyzer(it, luminosityAnalyzer!!)
                    }
                }
            // 预览
            val preview = Preview.Builder()
                // .setTargetResolution(size)
                .setTargetAspectRatio(ratio)
                .build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            // 摄像头选择
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            // 绑定
            try {
                cameraProvider?.unbindAll()
                camera = cameraProvider?.bindToLifecycle(
                    phoneScannerActivity,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
                light(false)
                onScanStart()
            } catch (exception: Exception) {
                // bind fail
            }
        }, ContextCompat.getMainExecutor(phoneScannerActivity))
    }

    fun restartPreviewAfterDelay() {

        luminosityAnalyzer?.let {
            onScanStart()
            it.resume()
        }
    }

    fun light(on: Boolean) {
        val future = camera?.cameraControl?.enableTorch(on)
        future?.addListener({

        }, ContextCompat.getMainExecutor(phoneScannerActivity))
    }

    /**
     * 获取状态栏的高度
     *
     * @param context Context
     * @return Int
     */
    private fun getStatusBarHeight(context: Context): Int {

        var statusBarHeight = 0

        val resourceId = context.resources
            .getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }

        return statusBarHeight
    }

    /**
     * 获取屏幕宽度
     *
     * @return Int
     */
    private fun getScreenWidth(context: Context) = context.resources.displayMetrics.widthPixels
}

typealias IScanResult = (raw: ByteArray, code: String) -> Unit

private class LuminosityAnalyzer(
    private val listener: IScanResult,
    private val statusBarHeightRelative: Float // 状态栏高度与屏幕宽度的比值
) : ImageAnalysis.Analyzer {

    private val multiFormatReader = ReaderProvider.provide()

    /**
     * 是否暂停解析
     */
    private var paused = false

    /**
     * 启动解析
     */
    fun resume() {
        paused = false
    }

    /**
     * 暂停解析
      */
    fun pause() {
        paused = true
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    override fun analyze(image: ImageProxy) {
        if (paused) {
            image.close()
            return
        }
        // 将 buffer 数据写入数组
        val data = image.planes[0].buffer.toByteArray()
        // 获取图片宽高，横屏角度
        val srcW = image.width // 横屏角度的宽
        val srcH = image.height // 横屏角度的高
        // 640 x 480
        // Log.logNormal("[debug]", "横屏角度 - image w=$srcW, h=$srcH")
        // 旋转为竖屏角度
        val rotatedData = ByteArray(data.size)
        var j: Int
        var k: Int
        for (y in 0 until srcH) {
            for (x in 0 until srcW) {
                j = x * srcH + srcH - y - 1
                k = x + y * srcW
                rotatedData[j] = data[k]
            }
        }
        /**
         *         srcH
         * ┌-------480-------┬-----------------┐
         * │                 │                 │
         * │                 │                 │
         * ├-----------------┼----┐            │
         * │ *************** │    │            │
         * │ *** 预览区域 ** │ previewH       640 - srcW
         * │ *************** │    │            │
         * ├-----------------┼----┘            │
         * │                 │                 │
         * │                 │                 │
         * └-----------------┴-----------------┘
         * 预览高度
         * previewH = parsingH + 2 * padding + statusBarHeight
         */

        // 设置数据范围

        val statusBarHeight = (statusBarHeightRelative * srcH).toInt()
        val padding = (20F / 360F * srcH).toInt()
        val parsingW = srcH - 2 * padding
        val tParsingH = (srcH / 4F * 3F).toInt()
        val parsingL = padding * 1
        val parsingT = ((srcW - tParsingH) / 2F + statusBarHeight + padding).toInt()
        val parsingH = tParsingH - 2 * padding - statusBarHeight
        val source = PlanarYUVLuminanceSource(
            rotatedData,
            srcH,
            srcW,
            parsingL,
            parsingT,
            parsingW,
            parsingH,
            false
        )
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            val result = multiFormatReader.decode(bitmap)
            listener(result.rawBytes ?: Util.source2Bytes(source), result.text)
            pause()
            // listener(Util.source2Bytes(source), result.text)
        } catch (e: NotFoundException) {
            // 识别不到
        } finally {
            image.close()
        }
    }
}
