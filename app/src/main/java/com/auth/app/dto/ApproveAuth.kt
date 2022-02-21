package com.auth.app.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class ApproveAuth(
    @JsonProperty("authRequestId")
    val AuthRequestId:UUID?,
    @JsonProperty("customerId")
    val CustomerId:UUID,
    @JsonProperty("approved")
    val Approved:Boolean?
)