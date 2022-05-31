package com.lyy.scanner.phone

import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.Reader
import java.util.*

/**
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-11-04
 */
object ReaderProvider {

    fun provide(): Reader {
        val hints: MutableMap<DecodeHintType, Any?> = EnumMap(
            DecodeHintType::class.java
        )
        val decodeFormats: MutableCollection<BarcodeFormat> = ArrayList()
        decodeFormats.addAll(EnumSet.of(BarcodeFormat.AZTEC))
        decodeFormats.addAll(EnumSet.of(BarcodeFormat.PDF_417))

        // 一维码
        // decodeFormats.addAll(DecodeFormatManager.getBarCodeFormats());
        // 二维码
        // decodeFormats.addAll(DecodeFormatManager.getQrCodeFormats());
        // 所有
        decodeFormats.addAll(DecodeFormatManager.getBarCodeFormats())
        decodeFormats.addAll(DecodeFormatManager.getQrCodeFormats())
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        val formatReader = MultiFormatReader()
        // hints[DecodeHintType.CHARACTER_SET] = "UTF-8"
        formatReader.setHints(hints)
        return formatReader
    }
}
