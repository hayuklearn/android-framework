package com.lyy.scanner

/**
 *
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-09-30
 */
interface ScannerListener {

    /**
     * 扫码加载
     *
     * @param loading Boolean
     */
    fun onScanLoading(loading: Boolean)

    /**
     * 扫码获得结果
     *
     * @param raw ByteArray?
     * @param code String?
     */
    fun onScanRead(raw: ByteArray?, code: String?)
}
