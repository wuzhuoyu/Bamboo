package com.yuu.android.component.bamboo.paho

import com.blankj.utilcode.util.ObjectUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.yuu.android.component.bamboo.config.FlowMqttConnectOptions
import com.yuu.android.component.bamboo.config.enums.FlowMqttQos
import com.yuu.android.component.bamboo.model.FlowMqttMessage
import org.eclipse.paho.client.mqttv3.*


/**
 * @ClassName : FlowMqttAndroidClient
 * @Description: MqttAndroidClient 子类
 * @Author: WuZhuoyu
 * @Date: 2021/4/29 15:53
 */

class FlowMqttAndroidClient : MqttAsyncClient, IFlowMqttAndroidClient {

    constructor(options: FlowMqttConnectOptions) : this(options, null)

    /**
     *
     * @param serverURI broker服务器地址
     * @param clientId 客户端id
     * @param persistence 消息缓存的方式 可空
     * ```
     * if (this.persistence == null) {
     *      this.persistence = new MemoryPersistence();
     *  }
     * ```
     * */
    constructor(
        options: FlowMqttConnectOptions,
        persistence: MqttClientPersistence?
    ) : super(options.brokerServerUrl, options.clientId, persistence) {

        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        if (ObjectUtils.isEmpty("测试")) {
            Logger.t("测试")
        }
    }


    override fun connectBroker(
        options: FlowMqttConnectOptions,
        callback: IFlowMqttActionListener?
    ): IMqttToken {
        //进行链接配置
        val connectOptions = MqttConnectOptions()
        connectOptions.apply {
            mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1

            //如果为false(flag=0)，Client断开连接后，Server应该保存Client的订阅信息
            //如果为true(flag=1)，表示Server应该立刻丢弃任何会话状态信息
            //设置清空Session，false表示服务器会保留客户端的连接记录，true表示每次以新的身份连接到服务器
            isCleanSession = options.isCleanSession ?: MqttConnectOptions.CLEAN_SESSION_DEFAULT

            //设置用户名和密码
            userName = options.account ?: ""
            password = options.password?.toCharArray()?: "".toCharArray()

            //设置连接超时时间
            connectionTimeout = options.connectionTimeout?:MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT

            //设置心跳发送间隔时间，单位秒
            keepAliveInterval = options.keepAliveInterval?:MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT

            // 客户端是否自动尝试重新连接到服务器
            isAutomaticReconnect = options.isAutomaticReconnect?:false



            //设置遗嘱
            setWill(
                options.setWillTopic ?: "set_will_topic",
                (options.setWillPayload ?: "set_will_payload").toByteArray(),
                options.setWillQos ?: FlowMqttQos.EXACTLY_ONCE.value(),
                options.setWillRetained ?: false
            )

        }

        return super.connect(connectOptions, null, callback)
    }

    override fun disconnectBroker(callback: IFlowMqttActionListener?): IMqttToken {
        return super.disconnect(null, callback)
    }

    override fun publishMessage(
        flowMqttMessage: FlowMqttMessage,
        callback: IFlowMqttActionListener?
    ): IMqttDeliveryToken {
        val topic = flowMqttMessage.getMessageTopic()
        val payload = flowMqttMessage.getMessagePayload()
        val qos = flowMqttMessage.getMessageQoS()
        val retained = flowMqttMessage.isRetained()
        return super.publish(topic, payload, qos.value(), retained, null, callback)
    }

    override fun subscribeMessage() {
    }

    override fun subscribeTopic(
        flowMqttMessage: FlowMqttMessage,
        actionListener : IFlowMqttActionListener?,
        messageListener: IFlowMqttMessageListener?
    ): IMqttToken {
        val topicFilters: String = flowMqttMessage.getMessageTopic() ?: ""
        val qos: Int = flowMqttMessage.getMessageQoS().value()
        return super.subscribe(topicFilters, qos, null, actionListener,messageListener)
    }

    override fun unsubscribeTopic(
        flowMqttMessage: FlowMqttMessage,
        callback: IFlowMqttActionListener?
    ): IMqttToken {

        val topicFilters: String = flowMqttMessage.getMessageTopic() ?: ""

        return super.unsubscribe(topicFilters, null, callback)
    }
}