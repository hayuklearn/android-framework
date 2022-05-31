package com.lyy.scanner.phone

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Size
import androidx.camera.core.AspectRatio
import com.google.zxing.PlanarYUVLuminanceSource
import java.io.ByteArrayOutputStream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-11-04
 */
object Util {

    private const val RATIO_4_3_VALUE = 4.0 / 3.0
    private const val RATIO_16_9_VALUE = 16.0 / 9.0

    /**
     * 根据 YUV 图像生成缩略图
     *
     * @param source
     * @return
     */
    fun source2Bytes(source: PlanarYUVLuminanceSource): ByteArray {
        val pixels = source.renderThumbnail()
        val width = source.thumbnailWidth
        val height = source.thumbnailHeight
        val bitmap = Bitmap
            .createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888)
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)
        return out.toByteArray()
    }

    fun bytes2Bitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * 根据预览框尺寸挑选最适合的摄像头分辨率长宽比 4x3 or 16x9
     *
     * @param previewW Int
     * @param previewH Int
     * @return Int
     */
    fun aspectRatio(previewW: Int, previewH: Int): Int {

        val previewRatio = max(previewW, previewH).toDouble() / min(previewW, previewH)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    fun calculateSize(previewW: Int, previewH: Int): Size {

        val screenAspectRatio = aspectRatio(previewW, previewH)
        val newPreviewH = if (screenAspectRatio == AspectRatio.RATIO_16_9) {
            (previewW * RATIO_16_9_VALUE).toInt()
        } else {
            (previewW * RATIO_4_3_VALUE).toInt()
        }
        return Size(previewW, newPreviewH)
    }
}
