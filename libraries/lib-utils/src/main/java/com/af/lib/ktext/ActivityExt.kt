package com.af.lib.ktext

import android.app.Activity
import android.widget.Toast

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
 * @date 2022-05-17
 */

/**
 * toast
 *
 * @receiver Activity
 * @param message String?
 */
fun Activity.toast(message: String?) {

    message?.let {
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
    }
}