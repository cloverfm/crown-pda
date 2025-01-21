package com.sf.cwms.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel :ViewModel(){
    protected val compositeDisposabe = CompositeDisposable();
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading : LiveData<Boolean>get() = _isLoading

    override fun onCleared(){
        compositeDisposabe.dispose()
        super.onCleared()
    }

    protected fun showProgress(){
        _isLoading.value = true
    }
    protected fun hideProgress(){
        _isLoading.value = false
    }

}