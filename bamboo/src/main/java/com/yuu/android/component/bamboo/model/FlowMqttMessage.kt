package com.yuu.android.component.bamboo.model

import com.yuu.android.component.bamboo.config.enums.FlowMqttQos
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


/**
 * @ClassName : FlowMqttMessage
 * @Description: message 消息实体
 * @Author: WuZhuoyu
 * @Date: 2021/4/29 11:36
 */


class FlowMqttMessage private constructor(
    private val topic: String?,
    private var message: MqttMessage
) : IFlowMqttMessage {

    private constructor(topic: String?, payload: ByteArray, qos: FlowMqttQos, retained: Boolean) : this(
        topic,
        MqttMessage(payload).apply {
            this.qos = qos.value()
            this.isRetained = retained
        }
    )

    companion object {

        fun createTopicMessage(topic: String): FlowMqttMessage {
            return FlowMqttMessage(
                topic = topic,
                payload = "".toByteArray(StandardCharsets.UTF_8),
                qos = FlowMqttQos.EXACTLY_ONCE,
                retained  = false
            )
        }

        fun create(topic: String? = null, message: MqttMessage): FlowMqttMessage {
            return FlowMqttMessage(topic, message)
        }

        fun create(
            topic: String? = null, payload: String, charset: Charset = StandardCharsets.UTF_8,
            qos: FlowMqttQos = FlowMqttQos.EXACTLY_ONCE,
            retained: Boolean = false
        ): FlowMqttMessage {
            return FlowMqttMessage(topic, payload.toByteArray(charset), qos, retained)
        }

        fun create(
            payload: ByteArray,
            qos: FlowMqttQos = FlowMqttQos.EXACTLY_ONCE,
            retained: Boolean = false,
            topic: String? = null
        ): FlowMqttMessage {
            return FlowMqttMessage(topic, payload, qos, retained)
        }
    }


    override fun getMessageTopic(): String? = topic

    override fun getMessageId(): Int = message.id

    override fun getMessagePayload(): ByteArray = message.payload

    override fun getMessageQoS(): FlowMqttQos = FlowMqttQos.valueOf(message.qos)

    override fun isRetained(): Boolean = message.isRetained

    override fun toString(): String = message.toString()
}
