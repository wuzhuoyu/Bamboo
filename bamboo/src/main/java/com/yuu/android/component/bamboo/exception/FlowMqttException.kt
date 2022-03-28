package com.yuu.android.component.bamboo.exception


/**
 * @ClassName : FlowMqttException
 * @Description: 异常信息，手动定义类型，方便异步流处理
 * @Author: WuZhuoyu
 * @Date: 2021/4/29 11:36
 */

open class FlowMqttException(message: String?, cause: Throwable?=null) : RuntimeException(message, cause) {




    companion object {
        private const val serialVersionUID = 6374001815972669318L

        @JvmField
        val brokerUrlNull = FlowMqttException("broker server url can not null")

        @JvmField
        val clientIsNull = FlowMqttException("client can not null")

        @JvmField
        val optionsNull = FlowMqttException("MQTT options is null")

        @JvmField
        val msgTopicNull = FlowMqttException("MQTT message topic is null")

        @JvmField
        val brokerConnectFailure = FlowMqttException("broker connection failed")

        @JvmField
        val brokerDisconnectFailure = FlowMqttException("broker disconnection failed")

        @JvmField
        val brokerRestartConnectFailure = FlowMqttException("broker restart connection failed")

        @JvmField
        val brokerHasBeenDisConnect = FlowMqttException("broker has been disconnected, no need to repeat the operation")

        @JvmField
        val brokerHasBeenConnecting = FlowMqttException("broker connecting, please do not disconnect at this time")

        @JvmField
        val brokerOffline = FlowMqttException("broker offline")

    }
}
