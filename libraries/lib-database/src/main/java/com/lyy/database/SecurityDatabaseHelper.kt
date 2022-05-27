package com.lyy.database

import android.content.Context
import org.litepal.LitePal

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
 * @see EncryptedItem
 *
 * 库表结构
 *
 * |  id              |  content                          |  type                    |  uniqueid                           |
 * |:----------------:|:---------------------------------:|:------------------------:|:-----------------------------------:|
 * | {sqlite 自增 ID} | {对象 json string 加密后的字符串} | {对象 class simple name} | {对象 class simple name}-{对象主键} |
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-03-02
 */
object SecurityDatabaseHelper {

    fun init(context: Context) {

        LitePal.initialize(context)
    }

    inline fun <reified T> saveOrUpdate(t: T, businessUniqueId: String): Boolean {

        val item = Security.toEncryptedItem(businessUniqueId, t)
        return item.saveOrUpdate()
    }

    inline fun <reified T> delete(): Int {

        val type =  T::class.java.simpleName
        return LitePal.deleteAll(EncryptedItem::class.java, "type = ?", type)
    }

    inline fun <reified T> delete(t: T, businessUniqueId: String): Int {

        val item = Security.toEncryptedItem(businessUniqueId, t)
        return item.delete()
    }

    inline fun <reified T> query(): List<T> {

        val type =  T::class.java.simpleName
        val list =  LitePal.where("type = ?", type).find(EncryptedItem::class.java)
        return list.mapNotNull {
            Security.fromEncryptedItem(it)
        }
    }

    /**
     * 查询
     *
     * @param businessUniqueId String 业务主键
     * @return T
     */
    inline fun <reified T> query(businessUniqueId: String): T? {

        val uniqueId = "${T::class.java.simpleName}-$businessUniqueId"
        val target = LitePal.where("uniqueId = ?", uniqueId).findFirst(EncryptedItem::class.java)
        return Security.fromEncryptedItem(target)
    }

    // ... 补充其它操作
}