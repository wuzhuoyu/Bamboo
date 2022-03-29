package com.yuu.android.component.bamboo.sample

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuu.android.component.bamboo.FlowMqttClient
import com.yuu.android.component.bamboo.config.FlowMqttConnectOptions
import com.yuu.android.component.bamboo.model.FlowMqttMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


/**
 * @ClassName : MqttViewModel
 * @Description:
 * @Author: WuZhuoyu
 * @Date: 2021/7/28 12:01
 */

class MqttViewModel : ViewModel() {

    var data = MutableLiveData<String>()

    fun connectToBroker() {
        viewModelScope.launch {
            val options = FlowMqttConnectOptions.build {
                brokerServerUrl = "tcp://192.168.100.150:1883"
                account = "1234567"
                password = "1234567:1234567"
                connectionTimeout = 10
                // 设置会话心跳时间，单位为秒。客户端每隔10秒向服务端发送心跳包判断客户端是否在线
                keepAliveInterval = 10
                clientId = "flow_mqtt_client"
            }

            FlowMqttClient.connectToBrokerService(options)
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.d("MQTT", "connect broker error:${it.message}")
                }.collect {
                    Log.d("MQTT", "connect $it" )
                    data.postValue("connect $it")
                }
        }
    }

    fun disConnectToBroker() {
        viewModelScope.launch {
            FlowMqttClient.disConnectToBrokerService()
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.d("MQTT", "disconnect broker error:${it.message}")
                }.collect {
                    Log.d("MQTT", "disconnect $it" )
                    data.postValue("disconnect $it")
                }
        }
    }

    fun restartConnectToBroker() {
        viewModelScope.launch {
            FlowMqttClient.restartConnectToBrokerService()
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.d("MQTT", "restart connect broker error:${it.message}")
                }.collect {
                    Log.d("MQTT", "restart connect broker $it" )
                    data.postValue("restart connect broker $it")
                }
        }
    }

    fun closeClient() {
        viewModelScope.launch {
            FlowMqttClient.closeClient()
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.d("MQTT", "close client error:${it.message}")
                }.collect {
                    Log.d("MQTT", "close client $it")
                    data.postValue("close client $it")
                }
        }
    }

    fun subscribeBrokerServerTopic1() {
        viewModelScope.launch {
            val message = FlowMqttMessage.createTopicMessage(topic = "flow_mqtt_topic1")
            FlowMqttClient.subscribeBrokerServerTopic(message)
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.d("MQTT", "subscribe topic error:${it.message}")
                }.collect {
                    Log.d("MQTT", "subscribe topic1 $it")
                    data.postValue("subscribe topic1 $it")
                }
        }
    }


    fun subscribeBrokerServerTopic2() {
        viewModelScope.launch {
            val message = FlowMqttMessage.createTopicMessage(topic = "flow_mqtt_topic2")
            FlowMqttClient.subscribeBrokerServerTopic(message)
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.d("MQTT", "subscribe topic error:${it.message}")
                }.collect {
                    Log.d("MQTT", "subscribe topic2 $it")
                    data.postValue("subscribe topic2 $it")
                }
        }
    }

    fun unsubscribeBrokerServerTopic() {
        viewModelScope.launch {
            val message = FlowMqttMessage.createTopicMessage(topic = "flow_mqtt_topic")
            FlowMqttClient.unsubscribeBrokerServerTopic(message)
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.d("MQTT", "unsubscribe topic error:${it.message}")
                }.collect {
                    Log.d("MQTT", "unsubscribe topic $it")
                    data.postValue("unsubscribe topic $it")
                }
        }
    }


    fun publishMqttMessage() {
        viewModelScope.launch {
            val message = FlowMqttMessage.create(topic = "flow_mqtt_topic",payload = "mqtt test publish message")
            FlowMqttClient.publishMqttMessage(message)
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.d("MQTT", "publish message error:${it.message}")
                }.collect {
                    Log.d("MQTT", "publish message $it")
                    data.postValue("publish message $it")
                }
        }
    }

    fun subscribeMqttMessage() {
//        viewModelScope.launch {
//            FlowMqttClient.subscribeMqttMessage()
//                .flowOn(Dispatchers.IO)
//                .catch {
//                    Log.d("MQTT", "subscribe message error:${it.message}")
//                }.collect {
//                    Log.d("MQTT", "subscribe message $it")
//                    data.postValue("subscribe message $it")
//                }
//        }
    }

    fun subscribeMessagePublishStatus() {
        viewModelScope.launch {
            FlowMqttClient.subscribeMessagePublishStatus()
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.d("MQTT", "subscribe message publish status error:${it.message}")
                }.collect {
                    Log.d("MQTT", "subscribe message publish status ${it.getMessage()}")
                    data.postValue("subscribe message publish status ${it.getMessage()}")
                }
        }
    }


}