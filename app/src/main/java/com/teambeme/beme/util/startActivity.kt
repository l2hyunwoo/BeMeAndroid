package com.teambeme.beme.util

import android.app.Activity
import android.content.Context
import android.content.Intent

inline fun <reified T : Activity> Context.startActivity(otherMindsTitle: String) {
    val intent = Intent(this, T::class.java)
    intent.putExtra("otherMindsTitle", otherMindsTitle)
    startActivity(intent)
}