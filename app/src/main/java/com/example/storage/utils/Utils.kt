package com.example.storage.utils

import android.content.Context
import android.os.Build
import android.widget.Toast

object Utils {

    fun fireToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun sdk29AndUp(): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
    }
}