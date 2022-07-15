package com.singlesecurekey.app.converters

interface ConverterFactory<out C> {

    fun convert(): C
}