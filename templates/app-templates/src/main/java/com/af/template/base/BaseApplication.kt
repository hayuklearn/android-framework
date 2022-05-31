package com.af.template.base

import com.af.lib.pinyin.Pinyin
import com.af.lib.pinyin.dict.CnCityDict
import com.lyy.database.SecurityDatabaseHelper
import com.mod.lifecycle.BaseLifecycleApplication

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
 * @date 2022-05-20
 */
class BaseApplication : BaseLifecycleApplication() {

    override fun onCreate() {
        super.onCreate()
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(this)))
        SecurityDatabaseHelper.init(this)
        BaseService.tryStartMainService(this)
    }
}