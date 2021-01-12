package com.teambeme.beme.data.remote.datasource

import com.teambeme.beme.signup.model.RequestSignUp
import com.teambeme.beme.signup.model.ResponseNickDoubleCheck
import com.teambeme.beme.signup.model.ResponseSignUp

interface SignUpDataSource {
    suspend fun signUp(body: RequestSignUp): ResponseSignUp
    suspend fun nickDoubleCheck(nickName: String): ResponseNickDoubleCheck
}