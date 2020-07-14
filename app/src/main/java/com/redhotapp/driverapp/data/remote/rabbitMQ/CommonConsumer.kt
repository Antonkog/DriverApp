package com.example.trackandtrace.order.orderDetails.rabbitMq

import android.util.Log
import com.example.rabbitmqtest.ui.main.rabbitMQ.RabbitmqListener
import com.google.gson.Gson
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.redhotapp.driverapp.data.model.LatestOrder


class CommonConsumer(
    channel: Channel,
    private val listener: RabbitmqListener?,
    private val gson: Gson
) : DefaultConsumer(channel) {

    override fun handleDelivery(
        consumerTag: String?,
        envelope: Envelope?,
        properties: AMQP.BasicProperties?,
        body: ByteArray?
    ) {

        val value = body?.let { String(it) }
        println("-----Order value----- $value")

        try {
            val order: LatestOrder = gson.fromJson(value, LatestOrder::class.java)
            listener?.gotLatestOrder(order)
        } catch (e: Exception) {
            Log.e(this.javaClass.canonicalName, e.message)
        }
        value?.let {
            listener?.gotMessage(it)
        }
//        val orderUpdateRabbitMQ = Gson().fromJson(value, OrderUpdateRabbitMQ::class.java)
    }
}