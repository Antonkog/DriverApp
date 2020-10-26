package com.abona_erp.driverapp.ui.fdanger

import android.content.Context
import android.content.SharedPreferences
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

class DangerousGoodsViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val app: AppRepository,
    private val prefs: SharedPreferences,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    val TAG = "DangerousGoodsViewModel"


}