package com.REVUnits.Hitokoto

import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.utils.info
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import net.mamoe.mirai.console.command.registerCommand
import net.mamoe.mirai.console.plugins.ToBeRemoved
import net.mamoe.mirai.console.plugins.withDefaultWriteSave
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.event.events.MemberMuteEvent
import net.mamoe.mirai.event.events.MemberPermissionChangeEvent
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.PlainText
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.Member
import java.util.*

object main : PluginBase() {
    lateinit var data:Hitokoto
    //val config = loadConfig("config.yml")
    //val Keywords by config.withDefaultWriteSave{"一言"}
    //val AllowGroupNum by config.withDefaultWriteSave {"群号"}
    //val AllowGroupNum = config.getLongList("Allow Group Number")
    val client = OkHttpClient()

    override fun onLoad() {
        super.onLoad()


    }
    
    override fun onEnable() {
            ///registerCommand {
                /// name = "一言"
                /// alias = listOf("hitokotoPlugin")
                /// description = "一言插件命令"
                /// usage="["/hitokotoPlugin Enable"]在本群启用一言插件\n"+
                    ///   "["/hitokotoPlugin Disabled"]在本群禁用一言插件\n"}
        val Url = "https://v1.hitokoto.cn"




        logger.info("Hitokoto Plugin Enabled")
        subscribeAlways<MemberMuteEvent> {
            it.group.sendMessage("恭喜老哥${it.member.nameCardOrNick}喜提禁言套餐一份")

        }
        subscribeAlways<MemberJoinEvent> {
            it.group.sendMessage("欢迎${it.member.nameCardOrNick}加入本群")
        }
        subscribeGroupMessages {
            contains("一言"){
                val request = Request.Builder().url(Url)
                        .get()
                        .build()
                val response = client.newCall(request).execute()
                val content = response.body!!.string()
                var temp_type: String?=""
                data = Gson().fromJson(content, Hitokoto::class.java)
                data.type = temp_type
                temp_type = when (data.type){
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

                reply("""
                    今日一言：${data.sentense}
                    来自：${data.from}
                    类型：$temp_type
                    数据来源：v1.hitokoto.cn
                """.trimIndent())

            }
            ("列表")reply("""
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
            startsWith("类型"){
                var rtype: String?
                rtype = when (it){
                    "动画"->"a"
                    "动漫"->"b"
                    "游戏"->"c"
                    "文学"->"d"
                    "原创"->"e"
                    "网络"->"f"
                    "影视"->"h"
                    "诗词"->"i"
                    "网易云"->"j"
                    "哲学"->"k"
                    "抖机灵"->"l"
                    else->"g"

                }
                val typeRequest = Request.Builder()
                        .url("${Url}?c=${rtype}")
                        .get()
                        .build()
                val typeResponse = client.newCall(typeRequest).execute()
                val content = typeResponse.body!!.string()
                data = Gson().fromJson(content,Hitokoto::class.java)
                this.group.sendMessage("""
                    今日一言：${data.sentense}
                    来自：${data.from}
                    类型：$it
                    参数：$rtype
                    数据来源：v1.hitokoto.cn
                """.trimIndent())
            }

            ("Copyright")reply("""
                Design/Code By SamSong
                Engine By Mirai
                Hitokoto Source By hitokoto.cn
            """.trimIndent())
            contains("HitokotoInfo"){
                this.group.sendMessage("""
                    一*言：输出标准格式的一*言
                    列*表：输出类*型列*表
                    类*型：输出特定类*型的一言
                    Hitokoto*info：指令菜单
                    Copy*right：输出插件版权信息
                    P.S.本消息中所有*均为防止程序逻辑错误所使用的占位符，使用时不需输入
                """.trimIndent())
            }
        }

    }
    override fun onDisable() {
        super.onDisable()
        logger.info("Saving Data&&Config")
        //config["AllowGroupNum"] = AllowGroupNum
        //config.save()
    }
}

data class Hitokoto(
    @SerializedName("hitokoto")
    var sentense:String? = "",
    var type:String? = "",
    var uuid:String? = "",
    var from:String?= ""
)
