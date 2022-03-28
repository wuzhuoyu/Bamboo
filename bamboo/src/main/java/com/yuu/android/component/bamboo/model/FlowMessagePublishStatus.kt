package com.yuu.android.component.bamboo.model


/**
 * @ClassName : FlowMessagePublishStatus
 * @Description: Message 发送状态实体
 * @Author: WuZhuoyu
 * @Date: 2021/4/29 11:36
 */


data class FlowMessagePublishStatus(
    //是否发送完毕
    val isComplete: Boolean? = null,
    //所发消息实体
    val flowMqttMessage: FlowMqttMessage? = null
) {

    /**
     * 获取发送的消息结果
     * @return 是否发送完毕
     * */
    fun getMessagePublishResult(): Boolean? = isComplete

    /**
     * 获取发送的消息
     * */
    fun getMessage(): FlowMqttMessage? = flowMqttMessage
}
