package com.redhotapp.driverapp.ui.home

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    protected val disposables: io.reactivex.disposables.CompositeDisposable =
        io.reactivex.disposables.CompositeDisposable()

    override fun onCleared() {
        disposables.takeIf { !it.isDisposed }?.dispose()
        super.onCleared()
    }


}