package com.lyy.scanner

/**
 * 扫码流程生命周期
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-09-30
 */
interface IScannerLifecycle {

    /**
     * 扫码资源初始化
     */
    fun onScannerResourceInit()

    /**
     * 扫码开始
     */
    fun onScanStart()

    /**
     * 扫码结束
     */
    fun onScanStop()

    /**
     * 扫码超时
     */
    fun onScanTimeout()

    /**
     * 扫码资源冻结
     */
    fun onScannerResourceFreeze()

    /**
     * 扫码资源释放
     */
    fun onScannerResourceRelease()
}
