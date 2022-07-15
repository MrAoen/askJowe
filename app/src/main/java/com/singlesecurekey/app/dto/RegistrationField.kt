package com.singlesecurekey.app.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RegistrationField (
    @JsonProperty("key")
    val key:String,
    @JsonProperty("placeholder")
    val placeholder:String?,
    @JsonProperty("regext", defaultValue = "/.*/")
    val regexp:String?,
    @JsonProperty("label")
    val name:String,
    @JsonProperty("required")
    val required:Boolean,
    @JsonProperty("type")
    val type:String?,
    @JsonProperty("inputtype")
    val inputtype: String
)