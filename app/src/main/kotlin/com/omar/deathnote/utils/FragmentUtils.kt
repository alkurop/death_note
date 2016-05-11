package com.omar.deathnote.utils

import android.support.v4.app.Fragment

fun Fragment?.isAdded(): Boolean {
    return this?.isAdded ?: false
}
