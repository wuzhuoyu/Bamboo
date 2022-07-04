package com.yuu.android.component.bamboo.client

import android.util.Log
import com.yuu.android.component.bamboo.api.FlowMqttApi
import com.yuu.android.component.bamboo.config.FlowMqttConnectOptions
import com.yuu.android.component.bamboo.exception.FlowMqttException
import com.yuu.android.component.bamboo.model.FlowBrokerConnectStatus
import com.yuu.android.component.bamboo.model.FlowMessagePublishStatus
import com.yuu.android.component.bamboo.model.FlowMqttMessage
import com.yuu.android.component.bamboo.paho.FlowMqttAndroidClient
import com.yuu.android.component.bamboo.paho.IFlowMqttActionListener
import com.yuu.android.component.bamboo.paho.IFlowMqttMessageListener
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import org.eclipse.paho.client.mqttv3.*


/**
 * @ClassName : FlowMqttClient
 * @Description: MQTT api 实现类
 * @Author: WuZhuoyu
 * @Date: 2021/4/29 11:53
 */

open class FlowMqttClient : FlowMqttApi {

    /**当前MQTT连接客户端*/
    private var client: FlowMqttAndroidClient? = null

    /**当前MQTT连接参数*/
    private var options: FlowMqttConnectOptions? = null

    /**当前服务器连接状态*/
    private val brokerStatus: FlowBrokerConnectStatus by lazy {
        FlowBrokerConnectStatus(isConnected = false)
    }

    override fun connectToBrokerService(mqttConnectOptions: FlowMqttConnectOptions): Flow<Boolean> {
        return flowOf(mqttConnectOptions).map {
            //broker server url can not null
            if (it.brokerServerUrl.isNullOrBlank()) throw FlowMqttException.brokerUrlNull else it
        }.map {
            //broker can not duplicate connection or connecting
            if (brokerStatus.isConnected) throw FlowMqttException.brokerConnectFailure else it
        }.flatMapConcat<FlowMqttConnectOptions, Boolean> {
            callbackFlow {
                //create a client
                client = FlowMqttAndroidClient(options = it)

                //asynchronous callback result
                val mqttActionListener = object : IFlowMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        options = it
                        brokerStatus.connectedSuccess()
                        trySend(true)
                        close()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        brokerStatus.error = exception
                        close()
                        throw FlowMqttException("Broker代理服务器连接失败", cause = exception)
                    }
                }

                //start connect broker
                try {
                    client?.connectBroker(it, mqttActionListener)
                    brokerStatus.connectingSuccess()
                } catch (e: MqttException) {
                    brokerStatus.connectingFailure()
                    brokerStatus.error = e
                    throw FlowMqttException("Broker代理服务器连接失败", cause = e)
                }
                awaitClose {
                    Log.i("FlowMqtt", ":::连接broker流关闭")
                }
            }
        }.catch {
            if (this is FlowMqttException) {
                restartConnectToBrokerService()
            }
        }
    }

    override fun disConnectToBrokerService(): Flow<Boolean> {
        return flowOf(brokerStatus).map {
            if (brokerStatus.isConnected) it else throw FlowMqttException.brokerHasBeenDisConnect
        }.map {
            if (brokerStatus.isConnecting) throw FlowMqttException.brokerHasBeenConnecting else it
        }.flatMapConcat {
            callbackFlow {
                //asynchronous callback result
                val mqttActionListener = object : IFlowMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        brokerStatus.disconnectSuccess()
                        trySend(true)
                        close()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        brokerStatus.error = exception
                        close()
                        throw FlowMqttException("Broker代理服务器断开失败", cause = exception)
                    }
                }

                try {
                    client?.disconnectBroker(mqttActionListener)
                } catch (e: MqttException) {
                    brokerStatus.error = e
                    throw FlowMqttException("Broker代理服务器断开失败", cause = e)
                }
                awaitClose {
                    Log.i("FlowMqtt", ":::断开broker流关闭")
                }
            }
        }
    }

    override fun closeClient(): Flow<Unit> {
        return flowOf(client).map {
            it ?: throw FlowMqttException.clientIsNull
        }.map {
            try {
                client?.close()
                brokerStatus.disconnectSuccess()
            } catch (e: MqttException) {
                throw FlowMqttException("关闭客户端错误", cause = e)
            }
        }
    }

    override fun restartConnectToBrokerService(): Flow<Boolean> {
        return flowOf(client).map {
            it ?: throw FlowMqttException.clientIsNull
        }.map {
            if (!it.isConnected) options else throw FlowMqttException.brokerOffline
        }.map {
            it ?: throw FlowMqttException.optionsNull
        }.flatMapConcat {
            connectToBrokerService(it)
        }
    }

    override fun publishMqttMessage(flowMqttMessage: FlowMqttMessage): Flow<Boolean> {
        return flowOf(flowMqttMessage).map {
            if (brokerStatus.isConnected) it else throw FlowMqttException.brokerOffline
        }.flatMapConcat {
            callbackFlow {
                //asynchronous callback result
                val mqttActionListener = object : IFlowMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        trySend(true)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        close()
                        throw FlowMqttException("发布消息的错误", exception)
                    }
                }

                try {
                    client?.publishMessage(
                        flowMqttMessage = flowMqttMessage,
                        callback = mqttActionListener
                    )
                } catch (e: MqttException) {
                    throw FlowMqttException(message = "消息发送失败", cause = e)
                }

                awaitClose {
                    Log.i("FlowMqtt", "::: 发送消息流关闭")
                }

            }
        }


    }

    override fun subscribeBrokerServerTopic(flowMqttMessage: FlowMqttMessage): Flow<FlowMqttMessage> {
        return flowOf(flowMqttMessage).map {
            if (it.getMessageTopic().isNullOrBlank()) throw FlowMqttException.msgTopicNull else it
        }.map {
            if (brokerStatus.isConnected) it else throw FlowMqttException.brokerOffline
        }.flatMapConcat {
            callbackFlow {

                //asynchronous callback result
                val mqttActionListener = object : IFlowMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.i("FlowMqtt", "订阅主题成功!")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        close()
                        Log.i("FlowMqtt", "订阅主题失败! ")
                        throw FlowMqttException(message = "订阅主题失败", cause = exception)
                    }
                }

                val mqttMessageListener = object : IFlowMqttMessageListener {
                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        message?.let {
                            trySend(FlowMqttMessage.create(topic, it))
                        }
                    }
                }

                try {
                    client?.subscribeTopic(
                        flowMqttMessage = it,
                        actionListener = mqttActionListener,
                        messageListener = mqttMessageListener
                    )
                } catch (e: MqttException) {
                    close()
                    throw FlowMqttException(message = "订阅主题失败", cause = e)
                }

                awaitClose {
                    Log.i("FlowMqtt", "::: 订阅主题流关闭")
                }
            }
        }
    }

    override fun unsubscribeBrokerServerTopic(flowMqttMessage: FlowMqttMessage): Flow<Boolean> {
        return flowOf(flowMqttMessage).map {
            if (it.getMessageTopic().isNullOrBlank()) throw FlowMqttException.msgTopicNull else it
        }.map {
            if (brokerStatus.isConnected) it else throw FlowMqttException.brokerOffline
        }.flatMapConcat {
            callbackFlow {

                //asynchronous callback result
                val mqttActionListener = object : IFlowMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        trySend(true)
                        close()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        close()
                        throw FlowMqttException("取消订阅主题错误", exception)
                    }
                }

                try {
                    client?.unsubscribeTopic(flowMqttMessage = it, callback = mqttActionListener)
                } catch (e: MqttException) {
                    close()
                    throw FlowMqttException(message = "取消订阅主题失败", cause = e)
                }

                awaitClose {
                    Log.i("FlowMqtt", "::: 取消订阅主题流关闭")
                }
            }
        }
    }

    override fun subscribeConnectionStatus(): Flow<Boolean> {
        return flowOf(client).map {
            it ?: throw FlowMqttException.clientIsNull
        }.flatMapConcat {
            callbackFlow {
                val mqttCallback = object : MqttCallback {
                    override fun connectionLost(cause: Throwable?) {
                        Log.i("FlowMqtt", "Broker代理服务器丢失:::$cause")
                        brokerStatus.isConnected = false
                        trySend(false)
                    }

                    override fun messageArrived(topic: String?, message: MqttMessage) {}

                    override fun deliveryComplete(token: IMqttDeliveryToken) {}
                }
                try {
                    it.setCallback(mqttCallback)
                } catch (e: Exception) {
                    close()
                    throw FlowMqttException("订阅状态错误", e)
                }


                awaitClose {
                    Log.i("FlowMqtt", "::: 订阅连接状态流关闭")
                }
            }
        }
    }

    override fun subscribeMessagePublishStatus(): Flow<FlowMessagePublishStatus> {
        return flowOf(client).map {
            it ?: throw FlowMqttException.clientIsNull
        }.flatMapConcat {
            callbackFlow {

                val mqttCallback = object : MqttCallback {
                    override fun connectionLost(cause: Throwable?) {}

                    override fun messageArrived(topic: String?, message: MqttMessage) {}

                    override fun deliveryComplete(token: IMqttDeliveryToken) {
                        Log.i(
                            "FlowMqtt",
                            "消息发送完成：topic:${token.topics},message:${token.message},messageId${token.messageId}"
                        )
                        trySend(
                            FlowMessagePublishStatus(
                                isComplete = true,
                                flowMqttMessage = FlowMqttMessage.create(message = token.message)
                            )
                        )
                    }
                }

                try {
                    it.setCallback(mqttCallback)
                } catch (e: Exception) {
                    close()
                    throw FlowMqttException("发布消息状态错误", e)
                }


                awaitClose {
                    Log.i("FlowMqtt", "::: 订阅连接状态流关闭")
                }
            }
        }
    }

    override fun getMqttClient(): Flow<FlowMqttAndroidClient?> {
        return flowOf(client)
    }
}