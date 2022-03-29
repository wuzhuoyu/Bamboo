package com.yuu.android.component.bamboo.paho

import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttMessage

/**
 * @InterfaceName : IFlowMqttMessageListener
 * @Description: IFlowMqttMessageListener MQTT 消息监听
 * @Author: WuZhuoyu
 * @Date: 2021/7/26 23:34
 */

interface IFlowMqttMessageListener: IMqttMessageListener {
    override fun messageArrived(topic: String?, message: MqttMessage?) {
    }
}