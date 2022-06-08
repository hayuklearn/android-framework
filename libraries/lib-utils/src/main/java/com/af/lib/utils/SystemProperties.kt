package com.af.lib.utils

import android.annotation.SuppressLint
import android.util.Log

/**
 * =================================================================================================
 *     __
 *    / /  ___    __  __  ____ _  ____    __  __  ____ _  ____
 *   / /  / _ \  / / / / / __ `/ / __ \  / / / / / __ `/ / __ \
 *  / /  /  __/ / /_/ / / /_/ / / /_/ / / /_/ / / /_/ / / /_/ /
 * /_/   \___/  \__, /  \__,_/  \____/  \__, /  \__,_/  \____/
 *             /____/                  /____/
 * =================================================================================================
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-05-25
 */
@SuppressLint("PrivateApi")
object SystemProperties {

    private const val TAG = "system-properties"

    private const val roBuildType = "ro.build.type"
    private const val roBuildTags = "ro.build.tags"
    private const val roBuildUser = "ro.build.user"
    private const val roBuildHost = "ro.build.host"
    private const val roBuildExpectBootloader = "ro.build.expect.bootloader"
    private const val roBuildExpectBaseband = "ro.build.expect.baseband"
    private const val roBuildVersionRelease = "ro.build.version.release"
    private const val roBuildId = "ro.build.id"
    private const val roBuildVersionIncremental = "ro.build.version.incremental"
    private const val roBuildDisplayId = "ro.build.display.id"
    private const val roBuildFingerprint = "ro.build.fingerprint"

    private const val roProductName = "ro.product.name"
    private const val roProductDevice = "ro.product.device"
    private const val roProductBoard = "ro.product.board"
    private const val roProductManufacturer = "ro.product.manufacturer"
    private const val roProductBrand = "ro.product.brand"
    private const val roProductModel = "ro.product.model"

    private const val roBootimageBuildFingerprint = "ro.bootimage.build.fingerprint"
    private const val roBootloader = "ro.bootloader"
    private const val roBootHardwareSku = "ro.boot.hardware.sku"
    private const val roBootQemu = "ro.boot.qemu"
    
    private const val roHardware = "ro.hardware"
    
    private const val roHwTimeoutMultiplier = "ro.hw_timeout_multiplier"

    private const val roSerialno = "ro.serialno"
    private const val roBootSerialno = "ro.boot.serialno"

    private const val roSystemBuildFingerprint = "ro.system.build.fingerprint"
    
    private const val roVendorBuildFingerprint = "ro.vendor.build.fingerprint"

    private val systemProperties by lazy { Class.forName("android.os.SystemProperties") }
    private val get by lazy { systemProperties.getDeclaredMethod("get", String::class.java) }

    private fun get(property: String): String {

        val propertyValue = get.invoke(null, property) as String
        Log.d(TAG, "$property - $propertyValue")
        return propertyValue
    }

    fun getRoBuildType() = get(roBuildType)
    fun getRoBuildTags() = get(roBuildTags)
    fun getRoBuildUser() = get(roBuildUser)
    fun getRoBuildHost() = get(roBuildHost)
    fun getRoBuildExpectBootloader() = get(roBuildExpectBootloader)
    fun getRoBuildExpectBaseband() = get(roBuildExpectBaseband)
    fun getRoBuildVersionRelease() = get(roBuildVersionRelease)
    fun getRoBuildId() = get(roBuildId)
    fun getRoBuildVersionIncremental() = get(roBuildVersionIncremental)
    fun getRoBuildDisplayId() = get(roBuildDisplayId)
    fun getRoBuildFingerprint() = get(roBuildFingerprint)

    fun getRoProductName() = get(roProductName)
    fun getRoProductDevice() = get(roProductDevice)
    fun getRoProductBoard() = get(roProductBoard)
    fun getRoProductManufacturer() = get(roProductManufacturer)
    fun getRoProductBrand() = get(roProductBrand)
    fun getRoProductModel() = get(roProductModel)

    fun getRoBootimageBuildFingerprint() = get(roBootimageBuildFingerprint)
    fun getRoBootloader() = get(roBootloader)
    fun getRoBootHardwareSku() = get(roBootHardwareSku)
    fun getRoBootQemu() = get(roBootQemu)

    fun getRoHardware() = get(roHardware)

    fun getRoHwTimeoutMultiplier() = get(roHwTimeoutMultiplier)

    fun getRoSerialno() = get(roSerialno)
    fun getRoBootSerialno() = get(roBootSerialno)

    fun getRoSystemBuildFingerprint() = get(roSystemBuildFingerprint)

    fun getRoVendorBuildFingerprint() = get(roVendorBuildFingerprint)

    fun test() {

        getRoBuildType()
        getRoBuildTags()
        getRoBuildUser()
        getRoBuildHost()
        getRoBuildExpectBootloader()
        getRoBuildExpectBaseband()
        getRoBuildVersionRelease()
        getRoBuildId()
        getRoBuildVersionIncremental()
        getRoBuildDisplayId()
        getRoBuildFingerprint()

        getRoProductName()
        getRoProductDevice()
        getRoProductBoard()
        getRoProductManufacturer()
        getRoProductBrand()
        getRoProductModel()

        getRoBootimageBuildFingerprint()
        getRoBootloader()
        getRoBootHardwareSku()
        getRoBootQemu()

        getRoHardware()

        getRoHwTimeoutMultiplier()

        getRoSystemBuildFingerprint()

        getRoVendorBuildFingerprint()
    }
}