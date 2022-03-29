package com.yuu.android.component.bamboo.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import com.yuu.android.component.bamboo.R


class MainMQTTActivity : AppCompatActivity() {

    private val viewModel: MqttViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_mqtt)
        initListener()
    }

    private fun initListener() {

        viewModel.data.observe(this){
            findViewById<TextView>(R.id.tv_info).text = it
        }

        findViewById<Button>(R.id.btn_connect).setOnClickListener {
            viewModel.connectToBroker()
        }

        findViewById<Button>(R.id.btn_disconnect).setOnClickListener {
            viewModel.disConnectToBroker()
        }

        findViewById<Button>(R.id.btn_reconnect).setOnClickListener {
            viewModel.restartConnectToBroker()
        }

        findViewById<Button>(R.id.btn_close_client).setOnClickListener {
            viewModel.closeClient()
        }

        findViewById<Button>(R.id.btn_subscribe_topic1).setOnClickListener {
            viewModel.subscribeBrokerServerTopic1()
        }

        findViewById<Button>(R.id.btn_subscribe_topic2).setOnClickListener {
            viewModel.subscribeBrokerServerTopic2()
        }

        findViewById<Button>(R.id.btn_unsubscribe_topic).setOnClickListener {
            viewModel.unsubscribeBrokerServerTopic()
        }

        findViewById<Button>(R.id.btn_publish_message).setOnClickListener {
            viewModel.publishMqttMessage()
        }

        findViewById<Button>(R.id.btn_receive_message).setOnClickListener {
            viewModel.subscribeMqttMessage()
        }

        findViewById<Button>(R.id.btn_subscribe_message_publish_status).setOnClickListener {
            viewModel.subscribeMessagePublishStatus()
        }

    }
}