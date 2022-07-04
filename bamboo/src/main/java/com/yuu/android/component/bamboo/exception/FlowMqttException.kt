package com.yuu.android.component.bamboo.exception


/**
 * @ClassName : FlowMqttException
 * @Description: 异常信息，手动定义类型，方便异步流处理
 * @Author: WuZhuoyu
 * @Date: 2021/4/29 11:36
 */

open class FlowMqttException(message: String?, cause: Throwable?=null) : RuntimeException(message, cause) {


    companion object {

        @JvmField
        val brokerUrlNull = FlowMqttException("Broker代理服务器url不能为空")

        @JvmField
        val clientIsNull = FlowMqttException("客户端不能为空")

        @JvmField
        val optionsNull = FlowMqttException("MQTT参数为空")

        @JvmField
        val msgTopicNull = FlowMqttException("MQTT消息主题为空")

        @JvmField
        val brokerConnectFailure = FlowMqttException("Broker代理服务器连接失败")

        @JvmField
        val brokerDisconnectFailure = FlowMqttException("Broker代理服务器断开失败")

        @JvmField
        val brokerRestartConnectFailure = FlowMqttException("Broker代理服务器重启连接失败")

        @JvmField
        val brokerHasBeenDisConnect = FlowMqttException("Broker代理服务器已断开，无需重复操作")

        @JvmField
        val brokerHasBeenConnecting = FlowMqttException("Broker代理服务器正在连接中，请不要在此时断开连接")

        @JvmField
        val brokerOffline = FlowMqttException("Broker代理服务器离线")

    }
}
