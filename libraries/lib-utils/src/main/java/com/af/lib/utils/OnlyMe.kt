package com.af.lib.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

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
 * 实现多 Activity 跳转后，倒序 finish 所有 Activity
 *
 *
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-04-29
 */
object OnlyMe {

    private val usage = """
        // in SettingActivity ----------------------------------------------------------------------
        // 定义
        private val onlyMeLauncher = registerForActivityResult(OnlyMe.generateContract<ChangePwdActivity>()) { result ->
            if (null == result) {
                return@registerForActivityResult
            }
            if (result) {
                OnlyMe.process(this@SettingActivity)
            }
        }
        // 执行跳转
        onlyMeLauncher.launch(null)
        
        // in ChangePwdActivity --------------------------------------------------------------------
        // 结束界面
        finish()
        OnlyMe.process(this@ChangePwdActivity)
    """.trimIndent()

    const val KEY = "finish"

    inline fun <reified C> generateContract(): ActivityResultContract<Boolean, Boolean> {

        return object : ActivityResultContract<Boolean, Boolean>() {

            override fun createIntent(context: Context, input: Boolean?): Intent {

                return Intent(context, C::class.java).apply {
                    putExtra(KEY, input)
                }
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Boolean? {

                val data = intent?.getBooleanExtra(KEY, false)
                return if (resultCode == Activity.RESULT_OK && data != null) data
                else null
            }
        }
    }

    fun process(activity: Activity) {

        activity.setResult(Activity.RESULT_OK, activity.intent.apply {
            putExtra(KEY, true)
        })
        activity.finish()
    }
}