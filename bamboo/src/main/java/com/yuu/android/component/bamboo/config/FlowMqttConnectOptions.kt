package com.yuu.android.component.bamboo.config


/**
 * @ClassName : FlowMqttConnectOptions
 * @Description: MQTT基础配置
 * @Author: WuZhuoyu
 * @Date: 2021/4/29 10:31
 */

open class FlowMqttConnectOptions {

    var brokerServerUrl: String ?=null
    var account: String ?=null
    var password:String ?=null
    var clientId: String ?=null
    var connectionTimeout: Int?=null
    var keepAliveInterval : Int?=null
    var isCleanSession:Boolean?=null
    var isAutomaticReconnect:Boolean?=null
    var setWillTopic:String?=null
    var setWillPayload:String?=null
    var setWillQos:Int?=null
    var setWillRetained:Boolean?=null

}