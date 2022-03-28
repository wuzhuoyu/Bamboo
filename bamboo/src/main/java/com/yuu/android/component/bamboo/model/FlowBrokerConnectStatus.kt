package com.yuu.android.component.bamboo.model


/**
 * @ClassName :FlowBrokerConnectStatus
 * @Description: Broker 连接状态实体
 * @Author: WuZhuoyu
 * @Date: 2021/4/29 11:36
 */


data class FlowBrokerConnectStatus(
    //连接状态
    var isConnected: Boolean = false,

    //是否正在链接
    var isConnecting:Boolean = false,

    //连接是异常
    var error: Throwable?=null
    ) {

    /**
     * 获取连接时异常
     * */
    fun getConnectError():Throwable? = error


    /**
     * 连接 broker 成功
     * */
    fun connectedSuccess() {
        this.isConnected = true
        this.isConnecting = false
    }

    /**
     * 连接中 broker 成功
     * */
    fun connectingSuccess() {
        this.isConnected = false
        this.isConnecting = true
    }

    /**
     * 连接中 broker 成功
     * */
    fun connectingFailure() {
        this.isConnected = false
        this.isConnecting = false
    }

    /**
     * 断开连接 broker 成功
     * */
    fun disconnectSuccess() {
        this.isConnected = false
        this.isConnecting = false
    }

}
