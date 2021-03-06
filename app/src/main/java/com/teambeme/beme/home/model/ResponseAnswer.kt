package com.teambeme.beme.home.model

import com.google.gson.annotations.SerializedName

data class ResponseAnswer(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val answer: Answer
)
