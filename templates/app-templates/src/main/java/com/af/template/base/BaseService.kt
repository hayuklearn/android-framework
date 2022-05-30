package com.af.template.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.af.lib.utils.Task
import com.af.template.base.BaseService.Companion.TAG
import com.mod.lifecycle.LifecycleIntentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

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
 * @date 2022-05-30
 */
class BaseService : LifecycleIntentService(TAG) {

    companion object {

        const val TAG = "base-service"

        var task: TimerTask? = null

        // 服务是否运行中
        private var running = false

        fun tryStartMainService(context: Context) {

            if (running) {
                Log.w(
                    TAG,
                    "try start base service: base service is already running"
                )
                return
            }
            context.bindService(
                Intent(context, BaseService::class.java),
                BaseServiceConnection(),
                Context.BIND_AUTO_CREATE
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        running = true
        Log.d(TAG, "服务启动")
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return MainServiceBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.e(TAG, "服务退出")
        running = false
        super.onDestroy()
    }

    override fun onHandleNonNullIntent(intent: Intent) {
        Log.d(TAG, "服务开始处理业务")
    }

    inner class MainServiceBinder : Binder() {

        fun getService() = this@BaseService
    }

    private fun startTask(value: String): TimerTask {

        return Task.start({

            Task.stop(task)
            Log.d("task", value)
            return@start
        }, 1000, 5000, -1)
    }

    fun start() {

        CoroutineScope(Dispatchers.IO).launch {

            task = startTask("test")
        }
    }
}

class BaseServiceConnection : ServiceConnection {

    private var baseService: BaseService? = null

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

        if (service is BaseService.MainServiceBinder) {
            baseService = service.getService()
            baseService?.let {

                Log.d(TAG, "服务已连接")
            }
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {

        Log.e(TAG, "服务已断开")
        baseService = null
    }
}