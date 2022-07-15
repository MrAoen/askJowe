package com.singlesecurekey.app.dto.notification

import com.singlesecurekey.app.converters.ConverterFactory
import com.singlesecurekey.app.dto.Ads
import com.singlesecurekey.app.converters.PayloadDeserializer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.lang.reflect.Method
import java.util.*
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AuthRequest (
    @JsonProperty("ID")
    val id : String?,//UUID?,

    @JsonProperty("Customer")
    @JsonDeserialize(using = PayloadDeserializer::class)
    val customer: String?,//RegistrationResponce?,

    @JsonProperty("TimeCreateed")
    val timeCreated:Long?,

    @JsonProperty("Merch")
    @JsonDeserialize(using = PayloadDeserializer::class)
    val merch:String?,//Merch?,

    @JsonProperty("State")
    val state:Int?
    ) : ConverterFactory<Ads> {

    override fun convert(): Ads {
        return converter(this)
    }

    companion object{

        fun converter(baseObject: AuthRequest): Ads {
            val mapper = jacksonObjectMapper()
            var ads = Ads("", "")

            baseObject.merch?.let {
                val clazz: Class<*>? = findClass("Merch")
                clazz?.let {
                    val subObject = mapper.readValue(baseObject.merch, clazz)
                    subObject::class.memberProperties.forEach {
                        if (it.visibility == KVisibility.PUBLIC) {
                            if (it.name.lowercase().equals("title")) {
                                ads.title = it.getter.call(subObject) as String
                            } else if (it.name.lowercase().equals("body")) {
                                ads.body = it.getter.call(subObject) as String
                            } else {
                                val subClazz: Class<*>? = findClass(it.name)
                                subClazz?.apply {
                                    val subObject =
                                        mapper.readValue(it.getter.call(this) as String, subClazz)
                                    val methodSetText: Method? = this.methods.firstOrNull {
                                        it.name == "convert"
                                                && it.genericParameterTypes.size == 1
                                                && it.genericParameterTypes[0] == this
                                    }
                                    val subAds: Ads? =
                                        methodSetText?.invoke(subObject, subObject) as Ads
                                    subAds?.let {
                                        if (subAds.title.isNotEmpty()) {
                                            ads.title = subAds.title
                                        }
                                        if (subAds.body.isNotEmpty()) {
                                            ads.body = subAds.body
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return ads
        }
        private fun findClass(payloadClassName: String?) = try {
            Class.forName("com.singlesecurekey.app.dto.notification.${payloadClassName?.capitalize(Locale.ROOT)}")
        } catch (e: Exception) {
            null
        }
    }



}