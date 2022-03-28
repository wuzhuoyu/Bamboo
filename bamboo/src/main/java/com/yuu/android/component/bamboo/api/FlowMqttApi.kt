package com.yuu.android.component.bamboo.api

import com.yuu.android.component.bamboo.config.FlowMqttConnectOptions
import com.yuu.android.component.bamboo.model.FlowMessagePublishStatus
import com.yuu.android.component.bamboo.model.FlowMqttMessage
import com.yuu.android.component.bamboo.paho.FlowMqttAndroidClient
import kotlinx.coroutines.flow.Flow

/**
 * @InterfaceName : FlowMqttApi
 * @Description: 对外提供调用的api方法，返回值使用Flow包裹，调用者可使用协程异步流处理
 * @Author: WuZhuoyu
 * @Date: 2021/4/29 10:24
 */

interface FlowMqttApi {

    /**
     * 连接broker服务器
     * @param options 连接参数
     * @return Boolean 返回值
     * */
    fun connectToBrokerService(options: FlowMqttConnectOptions):Flow<Boolean>

    /**
     * 断开broker服务器
     * @return Boolean 返回值
     * */
    fun disConnectToBrokerService():Flow<Boolean>

    /**
     * 关闭客户端的连接状态
     * */
    fun closeClient():Flow<Unit>

    /**
     * 重新连接broker服务器
     * @return Boolean 返回值
     * */
    fun restartConnectToBrokerService():Flow<Boolean>

    /**
     * 发布消息
     * @param flowMqttMessage mqtt消息实体
     * */
    fun publishMqttMessage(flowMqttMessage: FlowMqttMessage):Flow<Boolean>

    /**
     * 订阅消息
     * @return FlowMqttMessage mqtt消息实体
     * */
    fun subscribeMqttMessage():Flow<FlowMqttMessage>

    /**
     * 订阅主题
     * @param flowMqttMessage mqtt消息实体
     * @return Boolean 返回值
     * */
    fun subscribeBrokerServerTopic(flowMqttMessage: FlowMqttMessage):Flow<Boolean>

    /**
     * 取消订阅主题
     * @param flowMqttMessage mqtt消息实体
     * @return Boolean 返回值
     * */
    fun unsubscribeBrokerServerTopic(flowMqttMessage: FlowMqttMessage):Flow<Boolean>

    /**
     * 订阅broker服务器连接状态
     * @return FlowConnectionStatus mqtt服务器连接状态实体
     * */
    fun subscribeConnectionStatus():Flow<Boolean>

    /**
     * 订阅消息发送状态
     * @return FlowMessagePublishStatus 消息发送状态实体
     */
    fun subscribeMessagePublishStatus():Flow<FlowMessagePublishStatus>


    /**
     * 获取MQTT连接客户端
     */
    fun getMqttClient(): Flow<FlowMqttAndroidClient?>
}