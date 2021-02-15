package com.emel.app.ui.flows.authentication

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.emel.app.R
import com.emel.app.network.api.adapter.Status
import com.emel.app.network.api.requests.LoginRequest
import com.emel.app.ui.base.BaseActivity
import com.emel.app.ui.common.NavigationManager
import com.emel.app.ui.widgets.DialogUtils
import com.emel.app.utils.LoadingUtils
import com.emel.app.utils.setRefreshToken
import com.emel.app.utils.setToken
import kotlinx.android.synthetic.main.activity_authentication.*
import javax.inject.Inject


class AuthenticationActivity : BaseActivity<AuthenticationActivityVM>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var navigationManager: NavigationManager

    override fun layoutToInflate() = R.layout.activity_authentication

    override fun defineViewModel() =
        ViewModelProviders.of(this, viewModelFactory).get(AuthenticationActivityVM::class.java)


    override fun doOnCreated() {
        loginButton.setOnClickListener {
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()

            val loginRequest = LoginRequest(usernameText, passwordText)
            viewModel.login(loginRequest).observe(this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        saveTokensInSharedPreferences(it.data!!.token, it.data.refreshToken)
                        navigationManager.goToMainScreen(applicationContext)
                        LoadingUtils.dismiss()
                        finish()
                    }
                    Status.LOADING -> LoadingUtils.showLoading(supportFragmentManager)
                    Status.ERROR -> {
                        LoadingUtils.dismiss()
                        Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })
        }

        register.setOnClickListener {
            val browseintent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://parkingmetermanager.azurewebsites.net/Identity/Account/Register")
            )
            startActivity(browseintent)
        }

        recoverPass.setOnClickListener {
            val browseintent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://parkingmetermanager.azurewebsites.net/Identity/Account/ForgotPassword")
            )
            startActivity(browseintent)
        }
    }

    private fun saveTokensInSharedPreferences(token: String, refreshToken: String) {
        setToken("Bearer $token")
        setRefreshToken(refreshToken)
    }
}