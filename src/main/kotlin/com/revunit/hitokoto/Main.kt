package com.revunit.hitokoto

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberMuteEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeGroupMessages
import okhttp3.OkHttpClient
import okhttp3.Request

object Main : PluginBase() {
    //val config = loadConfig("config.yml")
    //val Keywords by config.withDefaultWriteSave{"一言"}
    //val AllowGroupNum by config.withDefaultWriteSave {"群号"}
    //val AllowGroupNum = config.getLongList("Allow Group Number")
    val client = OkHttpClient()
    val url = "https://v1.hitokoto.cn"

    override fun onEnable() {
        ///registerCommand {
        /// name = "一言"
        /// alias = listOf("hitokotoPlugin")
        /// description = "一言插件命令"
        /// usage="["/hitokotoPlugin Enable"]在本群启用一言插件\n"+
        ///   "["/hitokotoPlugin Disabled"]在本群禁用一言插件\n"}

        logger.info("Hitokoto Plugin Enabled")
        subscribeAlways<MemberMuteEvent> {
            it.group.sendMessage("恭喜老哥${it.member.nameCardOrNick}喜提禁言套餐一份")

        }
        subscribeAlways<MemberJoinEvent> {
            it.group.sendMessage("欢迎${it.member.nameCardOrNick}加入本群")
        }
        subscribeGroupMessages {
            "一言" reply (getHitokoto())
            ("列表") reply ("""
                类型列表（参数|类型）
                    a | 动画
                    b | 动漫
                    c | 游戏
                    d | 文学
                    e | 原创
                    f | 网络
                    g | 其他
                    h | 影视
                    i | 诗词
                    j | 网易云
                    k | 哲学
                    l | 抖机灵
            """.trimIndent())
            startsWith("类型", removePrefix = true) {
                val rtype: String = when (it) {
                    "动画" -> "a"
                    "动漫" -> "b"
                    "游戏" -> "c"
                    "文学" -> "d"
                    "原创" -> "e"
                    "网络" -> "f"
                    "影视" -> "h"
                    "诗词" -> "i"
                    "网易云" -> "j"
                    "哲学" -> "k"
                    "抖机灵" -> "l"
                    else -> "g"
                }
                group.sendMessage(getHitokoto(rtype))
            }

            ("Copyright") reply ("""
                Design/Code By SamSong
                Engine By Mirai
                Hitokoto Source By hitokoto.cn
            """.trimIndent())
            "HitokotoInfo" reply ("""
                    一言：输出标准格式的一言
                    列表：输出类型列表
                    类型：输出特定类型的一言
                    Hitokotoinfo：指令菜单
                    Copyright：输出插件版权信息
                """.trimIndent())

        }
    }

    override fun onDisable() {
        super.onDisable()
        logger.info("Saving Data&&Config")
        //config["AllowGroupNum"] = AllowGroupNum
        //config.save()
    }

    fun getHitokoto(type: String = ""): String {
        val request = Request.Builder().url("$url?c=$type")
            .get()
            .build()
        val response = client.newCall(request).execute()
        val content = response.body!!.string()
        val data = Gson().fromJson(content, Hitokoto::class.java)
        val tempType = when (data.type) {
            "a" -> "动画"
            "b" -> "漫画"
            "c" -> "游戏"
            "d" -> "文学"
            "e" -> "原创"
            "f" -> "网络"
            "g" -> "其他"
            "h" -> "影视"
            "i" -> "诗词"
            "j" -> "网易云"
            "k" -> "哲学"
            "l" -> "抖机灵"
            else -> "其他"
        }
        data.type = tempType
        return """
            今日一言：${data.sentense}
            来自：${data.from}
            类型：$tempType
            数据来源：v1.hitokoto.cn
         """.trimIndent()
    }
}

data class Hitokoto(
    @SerializedName("hitokoto")
    var sentense: String? = "",
    var type: String? = "",
    var uuid: String? = "",
    var from: String? = ""
)
