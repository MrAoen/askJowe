package com.auth.app.converters

interface ConverterFactory<out C> {

    fun convert(): C
}