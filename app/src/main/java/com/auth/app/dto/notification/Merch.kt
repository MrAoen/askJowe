package com.auth.app.dto.notification

import com.auth.app.converters.ConverterFactory
import com.auth.app.dto.Ads
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Merch(
    @JsonProperty("Id")
    val id: UUID?,

    @JsonProperty("Merch")
    val merchName: String?,

    @JsonProperty("CustomerId")
    val customerId: String?

//    @JsonProperty("URL")
//    val url: String?,
//
//    @JsonProperty("Secret")
//    val secret: String?,
//
//    @JsonProperty("IsActive")
//    val isActive: Boolean?
): ConverterFactory<Ads> {
    override fun convert(): Ads {
        return converter(this)
    }
    companion object{
        fun converter(baseObject: Merch): Ads {
            return Ads("", baseObject.merchName ?: "")
        }
    }
}

