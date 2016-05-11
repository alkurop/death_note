package com.omar.deathnote.utils

import android.support.annotation.StringRes
import android.util.Log
import com.omar.deathnote.App

fun getString(@StringRes stringId: Int): String {
    return App.getContext().getString(stringId)
}

fun log(tag: String, message: String) {
    Log.d(tag, message)
}
