package com.example.rabbitmqtest.ui.main.rabbitMQ

import android.R.string




class SocketConstants {
    companion object {
        const val exchange = "yms_develop"
        const val hostName = "172.31.0.7"
        const val virtualHost = "YMS"
        const val userName = "YMS"
        const val password = "JunkersJu87_"
        const val exchangeType = "topic"
        const val routingKey = "Driver" //arrayOf<string>("Driver", "Yard", "OperationControl")
    //    RabbitMQ;HostName = "172.31.0.7"; VirtualHost = "YMS";Port = 5672; exchange type=topic, exchange name="yms_develop"
    }


}