package com.example.rabbitmqtest.ui.main.rabbitMQ

import com.abona_erp.driverapp.data.model.LatestOrder

interface RabbitmqListener {
    fun gotMessage(msg: String)
    fun gotLatestOrder(msg: LatestOrder)
}