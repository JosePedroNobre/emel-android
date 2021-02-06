package com.emel.app.network.api.adapter

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

abstract class NetworkBoundResource<ResultType>
@MainThread constructor(private val appExecutors: com.emel.app.network.api.adapter.AppExecutors) {

    companion object {
        private val TAG = com.emel.app.network.api.adapter.NetworkBoundResource::class.java.simpleName
    }

    private val result = MediatorLiveData<com.emel.app.network.api.adapter.Resource<ResultType>>()
    private val network = MediatorLiveData<ResultType>()

    init {
        requestFromNetwork()
    }

    private fun requestFromNetwork() {
        val apiResponse = createCall()

        setValue(com.emel.app.network.api.adapter.Resource.Companion.loading())

        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)

            when (response) {
                is com.emel.app.network.model.ApiSuccessResponse -> {
                    appExecutors.diskIO().execute {
                        val responseBody = processResponse(response)
                        saveCallResult(responseBody)

                        appExecutors.mainThread().execute {
                            result.addSource(network) { newData ->
                                setValue(
                                    com.emel.app.network.api.adapter.Resource.Companion.success(
                                        200,
                                        newData
                                    )
                                )
                            }

                            updateNetworkSource(responseBody)
                        }
                    }
                }
                is com.emel.app.network.model.ApiErrorResponse -> {
                    appExecutors.mainThread().execute {
                        result.addSource(network) { newData ->
                            setValue(
                                com.emel.app.network.api.adapter.Resource.Companion.error(
                                    response.errorMessage,
                                    response.errorCode,
                                    newData
                                )
                            )
                        }
                        updateNetworkSource(null)
                    }
                }
            }
        }
    }

    fun asLiveData() = result as LiveData<com.emel.app.network.api.adapter.Resource<ResultType>>

    @MainThread
    private fun setValue(newValue: com.emel.app.network.api.adapter.Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    @MainThread
    private fun updateNetworkSource(newValue: ResultType?) {
        network.value = newValue
    }

    @WorkerThread
    protected open fun processResponse(response: com.emel.app.network.model.ApiSuccessResponse<ResultType>) = response.body

    @WorkerThread
    protected open fun saveCallResult(data: ResultType?) {
    }

    @MainThread
    protected abstract fun createCall(): LiveData<com.emel.app.network.model.ApiResponse<ResultType>>
}