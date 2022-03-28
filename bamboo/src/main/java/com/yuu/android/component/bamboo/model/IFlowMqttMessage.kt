package com.yuu.android.component.bamboo.model

import com.yuu.android.component.bamboo.config.enums.FlowMqttQos

/**
 * @InterfaceName : IFlowMqttMessage
 * @Description: 消息对象操作api
 * @Author: WuZhuoyu
 * @Date: 2021/7/28 18:20
 */

interface IFlowMqttMessage {

    /**
     * 获取消息topic
     * */
    fun getMessageTopic(): String?

    /**
     * 获取消息id
     * */
    fun getMessageId(): Int

    /**
     * 获取消息负载,内容
     * */
    fun getMessagePayload(): ByteArray

    /**
     * 获取消息质量
     * */
    fun getMessageQoS(): FlowMqttQos

    /**
     * 获取消息遗留状态
     * */
    fun isRetained(): Boolean
}