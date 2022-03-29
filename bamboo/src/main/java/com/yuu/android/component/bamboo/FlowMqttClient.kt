package com.yuu.android.component.bamboo

import android.util.Log
import android.util.SparseArray
import com.yuu.android.component.bamboo.api.FlowMqttApi
import com.yuu.android.component.bamboo.config.FlowMqttConnectOptions
import com.yuu.android.component.bamboo.exception.FlowMqttException
import com.yuu.android.component.bamboo.model.FlowBrokerConnectStatus
import com.yuu.android.component.bamboo.model.FlowMessagePublishStatus
import com.yuu.android.component.bamboo.model.FlowMqttMessage
import com.yuu.android.component.bamboo.paho.FlowMqttActionListener
import com.yuu.android.component.bamboo.paho.FlowMqttAndroidClient
import com.yuu.android.component.bamboo.paho.IFlowMqttActionListener
import com.yuu.android.component.bamboo.paho.IFlowMqttMessageListener
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
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

object FlowMqttClient : FlowMqttApi {

    /**当前MQTT连接客户端*/
    private var client: FlowMqttAndroidClient? = null

    /**当前MQTT连接参数*/
    private var options: FlowMqttConnectOptions? = null

    /**当前服务器连接状态*/
    private val brokerStatus: FlowBrokerConnectStatus by lazy {
        FlowBrokerConnectStatus(isConnected = false)
    }

    /**订阅消息流*/
    private var subscribeMessageFlow = MutableSharedFlow<FlowMqttMessage>(
        replay = 0,
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.SUSPEND
    )

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
                        trySendBlocking(true).onFailure {
                            throw FlowMqttException("connect broker error", it)
                        }
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        brokerStatus.error = exception
                        cancel(CancellationException("connect broker error", exception))
                    }
                }

                //start connect broker
                try {
                    client?.connectBroker(it, mqttActionListener)
                    brokerStatus.connectingSuccess()
                } catch (e: MqttException) {
                    brokerStatus.connectingFailure()
                    brokerStatus.error = e
                    throw FlowMqttException("broker connect failure", cause = e)
                }
                awaitClose {}
            }
        }.catch {
            if (this is FlowMqttException){
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
                try {
                    client?.disconnectBroker(object : IFlowMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            brokerStatus.disconnectSuccess()
                            trySendBlocking(true).onFailure {
                                throw FlowMqttException("disconnect error", it)
                            }
                        }

                        override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                        ) {
                            brokerStatus.error = exception
                            cancel(CancellationException("disconnect error", exception))
                        }
                    })
                } catch (e: MqttException) {
                    brokerStatus.error = e
                    throw FlowMqttException("broker disconnect failure", cause = e)
                }
                awaitClose {}
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
                throw FlowMqttException("", cause = e)
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
                try {
                    client?.publishMessage(
                        flowMqttMessage = flowMqttMessage,
                        callback = object : IFlowMqttActionListener {
                            override fun onSuccess(asyncActionToken: IMqttToken?) {
                                trySendBlocking(true).onFailure {
                                    throw FlowMqttException("publish message error", it)
                                }
                            }

                            override fun onFailure(
                                asyncActionToken: IMqttToken?,
                                exception: Throwable?
                            ) {
                                cancel(CancellationException("publish message error", exception))
                            }
                        }
                    )
                } catch (e: MqttException) {
                    throw FlowMqttException(message = "消息发送失败", cause = e)
                }

                awaitClose {}

            }
        }


    }

    fun getSubscribeMessageFlow(): MutableSharedFlow<FlowMqttMessage> = subscribeMessageFlow


    override fun subscribeBrokerServerTopic(flowMqttMessage: FlowMqttMessage): Flow<FlowMqttMessage> {
        return flowOf(flowMqttMessage).map {
            if (it.getMessageTopic().isNullOrBlank()) throw FlowMqttException.msgTopicNull else it
        }.map {
            if (brokerStatus.isConnected) it else throw FlowMqttException.brokerOffline
        }.flatMapConcat {
            callbackFlow {
                client?.subscribeTopic(
                    flowMqttMessage = it,
                    actionListener = object : IFlowMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.i("MQTT","subscribe broker success! ")
                        }

                        override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                        ) {
                            cancel(
                                CancellationException(
                                    "subscribe topic error:${exception?.message}",
                                    exception
                                )
                            )
                        }
                    },
                    messageListener = object :IFlowMqttMessageListener{
                        override fun messageArrived(topic: String?, message: MqttMessage?) {

                            message?.let {
                                trySendBlocking(FlowMqttMessage.create(topic,it)).onFailure {throwable->
                                    throw FlowMqttException(
                                        "subscribe topic error : ${throwable?.message}",
                                        throwable
                                    )
                                }
                            }
                        }
                    }
                )
                awaitClose { }
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
                client?.unsubscribeTopic(
                    flowMqttMessage = it,
                    callback = object : IFlowMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            trySendBlocking(true).onFailure {
                                throw FlowMqttException("unsubscribe topic error", it)
                            }
                        }

                        override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                        ) {
                            cancel(CancellationException("unsubscribe topic error", exception))
                        }
                    }
                )
                awaitClose { }
            }
        }
    }

    override fun subscribeConnectionStatus(): Flow<Boolean> {
        return flowOf(client).map {
            it ?: throw FlowMqttException.clientIsNull
        }.flatMapConcat {
            callbackFlow {
                it.setCallback(object : MqttCallback {
                    override fun connectionLost(cause: Throwable?) {
                        Log.i("MQTT", "broker lost：cause:$cause")
                        brokerStatus.isConnected = false
                        trySendBlocking(false)
                    }

                    override fun messageArrived(topic: String?, message: MqttMessage) {}

                    override fun deliveryComplete(token: IMqttDeliveryToken) {}
                })

                awaitClose { }
            }
        }
    }

    override fun subscribeMessagePublishStatus(): Flow<FlowMessagePublishStatus> {
        return flowOf(client).map {
            it ?: throw FlowMqttException.clientIsNull
        }.flatMapConcat {
            callbackFlow {
                it.setCallback(object : MqttCallback {
                    override fun connectionLost(cause: Throwable?) {}

                    override fun messageArrived(topic: String?, message: MqttMessage) {}

                    override fun deliveryComplete(token: IMqttDeliveryToken) {
                        Log.i(
                            "MQTT",
                            "消息发送完成：topic:${token.topics},message:${token.message},messageId${token.messageId}"
                        )
                        trySendBlocking(
                            FlowMessagePublishStatus(
                                isComplete = true,
                                flowMqttMessage = FlowMqttMessage.create(message = token.message)
                            )
                        ).onFailure {
                            throw FlowMqttException("publish message status error", it)
                        }
                    }
                })

                awaitClose { }
            }
        }
    }

    override fun getMqttClient(): Flow<FlowMqttAndroidClient?> {
        return flowOf(client)
    }
}