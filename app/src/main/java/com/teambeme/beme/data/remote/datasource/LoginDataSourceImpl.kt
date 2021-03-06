package com.teambeme.beme.data.remote.datasource

import com.teambeme.beme.data.remote.api.LoginService
import com.teambeme.beme.login.model.RequestLogin
import com.teambeme.beme.login.model.ResponseLogin
import retrofit2.Call

class LoginDataSourceImpl(private val service: LoginService) : LoginDataSource {
    override fun login(nickname: String, password: String): Call<ResponseLogin> {
        return service.login(RequestLogin(nickname, password))
    }
}