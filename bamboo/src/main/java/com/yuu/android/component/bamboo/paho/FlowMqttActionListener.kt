package com.yuu.android.component.bamboo.paho

import org.eclipse.paho.client.mqttv3.IMqttToken


/**
 * @ClassName : FlowMqttActionListener
 * @Description:
 * @Author: WuZhuoyu
 * @Date: 2022/3/28 16:08
 */

class FlowMqttActionListener() :IFlowMqttActionListener {
    override fun onSuccess(asyncActionToken: IMqttToken?) {

    }

    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {

    }
}