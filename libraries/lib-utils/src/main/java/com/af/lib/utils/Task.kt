package com.af.lib.utils

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
 * 通用异步任务
 *
 * 第一版基于 java.util.TimerTask
 * TODO 后续考虑替换为 kotlin 协程
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-02-08
 */
object Task {

    /**
     * 启动一个定时任务
     *
     * @param runnable Runnable
     * @param delay Long ms
     * @param period Long ms
     * @param max Int 最大次数
     * @return TimerTask
     */
    fun start(runnable: Runnable, delay: Long = 0, period: Long = 5000, max: Int = -1): TimerTask {

        val task = object : TimerTask() {

            private var count = 0

            override fun run() {

                if (max > 0) {
                    if (count < max) {
                        count++
                    } else {
                        cancel()
                        return
                    }
                }
                runnable.run()
            }
        }
        Timer().schedule(task, delay, period)
        return task
    }

    /**
     * 终止一个定时任务
     *
     * @param task TimerTask?
     * @return Unit
     */
    fun stop(task: TimerTask?) {

        task?.cancel()
    }
}