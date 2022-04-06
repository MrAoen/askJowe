package com.auth.app.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ApproveAuth(

    @JsonProperty("authRequestId")
    val authRequestId:UUID?,
    @JsonProperty("customerId")
    val customerId:UUID,
    @JsonProperty("approved")
    val approved:Boolean?
)