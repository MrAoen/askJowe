package com.auth.app.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class RegistrationResponce(
    @get:JsonProperty("id")
    @JsonProperty("id")
    val customerId: UUID,

//    @get:JsonProperty("Email")
//    @JsonProperty("Email")
//    val email: String,
//
//    @get:JsonProperty("FirstName")
//    @JsonProperty("FirstName")
//    val firstName: String?,
//
//    @get:JsonProperty("LastName")
//    @JsonProperty("LastName")
//    val lastName: String?,
//
//    @get:JsonProperty("Phone")
//    @JsonProperty("Phone")
//    val phone: String?,
//
//    @get:JsonProperty("Lang")
//    @JsonProperty("Lang")
//    val lang: String?,
//
//    @get:JsonProperty("Pass")
//    @JsonProperty("Pass")
//    val pass: String?,

    @get:JsonProperty("secret")
    @JsonProperty("secret")
    val secret: String?
)