package com.singlesecurekey.app.dto.notification

import com.singlesecurekey.app.converters.ConverterFactory
import com.singlesecurekey.app.dto.Ads
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Merch(
    @JsonProperty("Id")
    val id: UUID?,

    @JsonProperty("Merch")
    val merchName: String?,

    @JsonProperty("CustomerId")
    val customerId: String?,

//    @JsonProperty("URL")
//    val url: String?,
//
//    @JsonProperty("Secret")
//    val secret: String?,
//
//    @JsonProperty("IsActive")
//    val isActive: Boolean?

    @JsonProperty("title")
    val title:String?

): ConverterFactory<Ads> {
    override fun convert(): Ads {
        return converter(this)
    }
    companion object{
        fun converter(baseObject: Merch): Ads {
            return Ads(baseObject.title?: "", baseObject.merchName ?: "")
        }
    }
}

