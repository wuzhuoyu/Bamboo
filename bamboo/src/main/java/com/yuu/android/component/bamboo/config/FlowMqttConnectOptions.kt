package com.yuu.android.component.bamboo.config

import org.eclipse.paho.client.mqttv3.MqttConnectOptions.MQTT_VERSION_3_1

/**
 * @ClassName : FlowMqttConnectOptions
 * @Description: MQTT基础配置
 * @Author: WuZhuoyu
 * @Date: 2021/4/29 10:31
 */

open class FlowMqttConnectOptions(builder: FlowMqttConnectOptionsBuilder,) {

    var mqttVersion = MQTT_VERSION_3_1
    var brokerServerUrl: String ?=null
    var userName: String ?=null
    var password:String ?=null
    var clientId: String ?=null
    var topics: Array<String> = arrayOf()
    var retryIntervalTime: Int = -1
    var connectionTimeout: Int = 30
    var keepAliveInterval : Int = 60

    init {
        this.mqttVersion = builder.mqttVersion
        this.brokerServerUrl = builder.brokerServerUrl
        this.userName = builder.account
        this.password = builder.password
        this.clientId = builder.clientId
        this.topics = builder.topics
        this.retryIntervalTime = builder.retryIntervalTime
        this.connectionTimeout = builder.connectionTimeout
        this.keepAliveInterval = builder.keepAliveInterval
    }

    companion object {
        inline fun build(block: FlowMqttConnectOptionsBuilder.() -> Unit) =
            FlowMqttConnectOptionsBuilder().apply(
                block
            ).build()
    }
}