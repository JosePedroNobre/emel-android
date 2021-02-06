package com.emel.app.ui.flows.authentication

import androidx.lifecycle.ViewModel
import com.emel.app.network.api.requests.LoginRequest
import com.emel.app.repository.EmelRepository
import javax.inject.Inject

class AuthenticationActivityVM @Inject constructor(private val emelRepository: EmelRepository) :
    ViewModel() {

    fun login(loginRequest: LoginRequest) =
        emelRepository.login(loginRequest)

}