package com.lyy.database

import org.litepal.LitePal
import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

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
 * 数据库条目
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-03-01
 *
 * @property uniqueId String 标识 ID
 * @property type String 条目类型
 * @property content String 条目加密内容
 * @constructor
 */
data class EncryptedItem(@Column(unique = true) val uniqueId: String, var type: String, var content: String): LitePalSupport() {

    override fun hashCode(): Int {

        var code = this.uniqueId.hashCode()
        code = code * 31 + this.type.hashCode()
        return code
    }

    override fun equals(other: Any?): Boolean {

        return if (other is EncryptedItem) {
            this.hashCode() == other.hashCode()
        } else {
            false
        }
    }

    /**
     * 插入或更新
     *
     * @return Boolean 是否成功
     */
    fun saveOrUpdate(): Boolean {

        return saveOrUpdate("uniqueId = ?", this.uniqueId)
    }

    /**
     * 删除
     *
     * @return Int 成功删除的数量
     */
    override fun delete(): Int {

        return LitePal.deleteAll(EncryptedItem::class.java, "uniqueId = ?", this.uniqueId)
    }

    /**
     * 从数据库表中查询
     *
     * @return Boolean
     */
    fun query(): Boolean {

        val list = LitePal.where("uniqueId = ?", this.uniqueId).find(EncryptedItem::class.java)
        return when (list.size) {
            0 -> {
                false
            }
            1 -> {
                this.type = list[0].type
                this.content = list[0].content
                true
            }
            else -> {
                throw RuntimeException("exception: more than one record was found !!!")
            }
        }
    }
}
