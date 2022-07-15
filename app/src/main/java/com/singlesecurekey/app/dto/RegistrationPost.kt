package com.singlesecurekey.app.dto

data class RegistrationPost(
    val email:String,
    val firstName:String,
    val lastName:String,
    val phone:String,
    val lang:String
)