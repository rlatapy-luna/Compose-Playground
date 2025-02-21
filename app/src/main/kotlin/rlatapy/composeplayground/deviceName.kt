package rlatapy.composeplayground

import android.os.Build

fun deviceName(): String = "${Build.MANUFACTURER} ${Build.PRODUCT}"
