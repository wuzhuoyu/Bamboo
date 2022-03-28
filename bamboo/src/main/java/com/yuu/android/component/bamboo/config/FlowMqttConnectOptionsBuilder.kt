package com.yuu.android.component.bamboo.config

import org.eclipse.paho.client.mqttv3.MqttConnectOptions


/**
 * @ClassName : FlowMqttConnectOptionsBuilder
 * @Description:
 * @Author: WuZhuoyu
 * @Date: 2021/4/29 11:15
 */

open class  FlowMqttConnectOptionsBuilder {

    var brokerServerUrl:String ?=null

    var account:String ?=null

    var password:String  ?=null

    var clientId:String ?=null

    var topics:Array<String> = arrayOf()

    var retryIntervalTime:Int = -1

    var connectionTimeout:Int = 60

    var keepAliveInterval:Int = 30

    // 设置Mqtt版本
    var mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1

    /**
     * 设置broker服务端地址
     * @param brokerServerUrl 地址
     */
    fun setBrokerServerUrl(brokerServerUrl: String): FlowMqttConnectOptionsBuilder {
        this.brokerServerUrl = brokerServerUrl
        return this
    }

    /**
     * 账号
     * @param account 账号
     */
    fun setAccount(account: String): FlowMqttConnectOptionsBuilder {
        this.account = account
        return this
    }

    /**
     * 密码
     * @param password 密码
     */
    fun setPassword(password: String): FlowMqttConnectOptionsBuilder {
        this.password = password
        return this
    }

    /**
     * 客户端Id，必须唯一
     * @param clientId 客户端id
     */
    fun setClientId(clientId: String): FlowMqttConnectOptionsBuilder {
        this.clientId = clientId
        return this
    }

    /**
     * 需要订阅的主题，连接成功后，会自动订阅
     * @param topics 主题集合
     */
    fun setTopics(topics: Array<String>): FlowMqttConnectOptionsBuilder {
        this.topics = topics
        return this
    }

    /**
     * 重试间隔时间，单位为秒
     * @param retryIntervalTime 重试间隔时间
     */
    fun setRetryIntervalTime(retryIntervalTime: Int): FlowMqttConnectOptionsBuilder {
        this.retryIntervalTime = retryIntervalTime
        return this
    }

    /**
     * 连接超时时间
     * @param connectionTimeout 连接超时时间
     */
    fun setConnectionTimeout(connectionTimeout: Int): FlowMqttConnectOptionsBuilder {
        this.connectionTimeout = connectionTimeout
        return this
    }

    /**
     * 保持活动时间，超过时间没有消息收发将会触发ping消息确认
     * @param keepAliveInterval 保持活动时间
     */
    fun setKeepAliveInterval(keepAliveInterval: Int): FlowMqttConnectOptionsBuilder {
        this.keepAliveInterval = keepAliveInterval
        return this
    }

    fun build(): FlowMqttConnectOptions = FlowMqttConnectOptions(this,)
}