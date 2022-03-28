package com.yuu.android.component.bamboo.model.enums

/**
 * @EnumName : FlowBrokerServerStatus
 * @Description:
 * @Author: WuZhuoyu
 * @Date: 2021/7/27 0:13
 */
@Deprecated("client 可以提供状态")
enum class FlowBrokerServerStatus(private val value: Int) {
    /**
     * 当前broker服务器处于连接上状态
     * */
    CONNECTED(0),

    /**
     * 当前broker服务器正在连接中... （此时还未连接上）
     * */
    CONNECTING(1),

    /**
     * 当前broker服务器断开连接
     * */
    DISCONNECT(2);

    /**
     * value值
     */
    fun value(): Int = value

    companion object {
        fun valueOf(value: Int) = values().first { it.value() == value }
    }
}