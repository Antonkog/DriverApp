package com.redhotapp.driverapp.data.remote

import com.redhotapp.driverapp.data.model.LatestOrder
import io.reactivex.rxjava3.core.Observable

interface ApiRepository {
    fun getLatestOrder(id: String): Observable<LatestOrder>
}