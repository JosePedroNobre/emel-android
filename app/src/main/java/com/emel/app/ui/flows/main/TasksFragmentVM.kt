package com.emel.app.ui.flows.main

import androidx.lifecycle.ViewModel
import com.emel.app.network.api.requests.TokenRequest
import com.emel.app.repository.EmelRepository
import com.emel.app.ui.base.BaseFragment
import javax.inject.Inject

class TasksFragmentVM @Inject constructor(private val emelRepository: EmelRepository) :
    ViewModel() {

    fun getAsssignedMalfunctions(token: String) =
        emelRepository.getMalFunctionsByUser(token)

    fun refreshToken(token: String, tokenRequest: TokenRequest) =
        emelRepository.refreshToken(token, tokenRequest)
}