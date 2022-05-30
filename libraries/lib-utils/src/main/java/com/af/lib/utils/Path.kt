package com.af.lib.utils

import android.content.Context
import android.os.Environment
import java.io.File

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
 * 路径提供方法
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-12-28
 */
object Path {

    private fun mkdirsIfNotExists(path: String) {

        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    /**
     * 安装包保存路径
     *
     * @param context Context
     * @return String e.g. "/storage/emulated/0/Android/data/com.lyy.retail.dynamic.test/files/Download/apk"
     */
    fun apkPath(context: Context): String {

        val path =
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/apk"
        mkdirsIfNotExists(path)
        return path
    }

    /**
     * 日志文件保存路径
     *
     * @param context Context
     * @return String e.g. "/storage/emulated/0/Android/data/com.lyy.retail.dynamic.test/files/log"
     */
    fun logPath(context: Context): String {

        val path = context.getExternalFilesDir(null)?.absolutePath + "/log"
        mkdirsIfNotExists(path)
        return path
    }

    /**
     * 配置文件保存路径
     *
     * @param context Context
     * @return String e.g. "/storage/emulated/0/Android/data/com.lyy.retail.dynamic.test/files/config"
     */
    fun configPath(context: Context): String {

        val path = context.getExternalFilesDir(null)?.absolutePath + "/config"
        mkdirsIfNotExists(path)
        return path
    }

    /**
     * SQLite 数据库文件导出路径
     *
     * @param context Context
     * @return String e.g. "/storage/emulated/0/Android/data/com.lyy.retail.dynamic.test/files/sqlite"
     */
    fun sqliteOutputPath(context: Context): String {

        return context.getExternalFilesDir(null)?.absolutePath + "/sqlite"
    }
}