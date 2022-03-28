package com.yuu.android.component.bamboo.paho

import com.yuu.android.component.bamboo.config.FlowMqttConnectOptions
import com.yuu.android.component.bamboo.model.FlowMqttMessage
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken

/**
 * @InterfaceName : IFlowMqttAndroidClient
 * @Description: Client 操作
 * @Author: WuZhuoyu
 * @Date: 2021/7/28 18:53
 */

interface IFlowMqttAndroidClient {

    /**
     * 连接broker服务器
     * */
    fun connectBroker(
        options: FlowMqttConnectOptions,
        callback: IFlowMqttActionListener?
    ): IMqttToken

    /**
     * 断开broker服务器连接
     * @param callback Mqtt行为回调
     * */
    fun disconnectBroker(callback: IFlowMqttActionListener?): IMqttToken

    /**
     * 发送 mqtt 消息
     * @param flowMqttMessage 消息对象
     * @param callback Mqtt行为回调
     * */
    fun publishMessage(
        flowMqttMessage: FlowMqttMessage,
        callback: IFlowMqttActionListener?
    ): IMqttDeliveryToken

    fun subscribeMessage()

    /**
     * 订阅消息
     * @param flowMqttMessage 消息对象
     * @param callback mqtt行为回调
     * */
    fun subscribeTopic(
        flowMqttMessage: FlowMqttMessage,
        callback: IFlowMqttActionListener?
    ): IMqttToken


    /**
     * 取消订阅主题*/
    fun unsubscribeTopic(
        flowMqttMessage: FlowMqttMessage,
        callback: IFlowMqttActionListener?
    ): IMqttToken
}