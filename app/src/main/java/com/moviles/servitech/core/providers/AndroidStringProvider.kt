package com.moviles.servitech.core.providers

import android.content.Context
import javax.inject.Inject

interface StringProvider {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg args: Any): String
}

class AndroidStringProvider @Inject constructor(
    private val context: Context
) : StringProvider {
    override fun getString(resId: Int): String = context.getString(resId)
    override fun getString(resId: Int, vararg args: Any): String = context.getString(resId, *args)
}