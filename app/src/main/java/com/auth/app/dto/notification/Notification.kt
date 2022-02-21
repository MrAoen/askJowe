package com.auth.app.dto.notification

import com.auth.app.converters.ConverterFactory
import com.auth.app.dto.Ads
import com.auth.app.converters.PayloadDeserializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.lang.reflect.Method
import java.util.*
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

data class Notification(

    @JsonProperty("Title")
    val title: String,
    @JsonProperty("MsgText")
    val msgText: String?,
//    @JsonProperty("TimePublish")
//    val timePublish: Long?,
//    @JsonProperty("TimeExpiration")
//    val timeExpiration: Long?,
//    @JsonProperty("Customers")
//    val customers: String?,
    @JsonProperty("TypeParams")
    val typeParams: String?,

    @JsonProperty("BodyParams")
    @JsonDeserialize(using = PayloadDeserializer::class)
    val bodyParams: String,

//    @JsonProperty("AppName")
//    val appName: String?,
//    @JsonProperty("IsPriority")
//    val isPriority: Boolean?,
//    @JsonProperty("Lang")
//    val lang: String?,
//    @JsonProperty("ChooseSilentNoisePlatforms")
//    val chooseSilentNoisePlatforms: ChooseSilentNoisePlatforms?

): ConverterFactory<Ads> {
    override fun convert(): Ads {
        return converter(this)
    }

    companion object {

        fun converter(baseObject:Notification):Ads{
            val ads = Ads(baseObject.title, "")
            val payloadClassName = baseObject.typeParams
            val clazz: Class<*>? = findClass(payloadClassName)
            clazz?.let {

                val subObject = jsonToClass(baseObject.bodyParams, clazz)
                subObject::class.memberProperties.forEach {
                    if (it.visibility == KVisibility.PUBLIC) {
                        when {
                            it.name.lowercase() == "title" -> {
                                ads.title = it.getter.call(subObject) as String
                            }
                            //it.name.lowercase() == "body" -> {
                            it.name.lowercase() == "merch" -> {
                                ads.body = it.getter.call(subObject) as String
                            }
                            else -> {
                                val subClazz: Class<*>? = findClass(it.name)
                                subClazz?.apply {
                                    val subString = it.getter.call(subObject) as String
                                    val subObject2 = jsonToClass(subString, this)
                                    val methodSetText: Method? = this.methods.firstOrNull {
                                        it.name == "convert"
                                    }
                                    updateAds(ads,methodSetText?.invoke(subObject2) as Ads)
                                }
                            }
                        }
                    }
                }
            }
            return ads
        }

        private fun updateAds(ads: Ads, adsNew: Ads) {
            adsNew.let {
                if (adsNew.title.isNotEmpty()) {
                    ads.title = adsNew.title
                }
                if (adsNew.body.isNotEmpty()) {
                    ads.body = adsNew.body
                }
            }
        }

        private fun jsonToClass(baseObject: String, clazz: Class<*>?):Any {
            val mapper = jacksonObjectMapper()
                .registerKotlinModule()
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            return try {
                mapper.readValue(mapper.readTree(baseObject).textValue(), clazz)
            } catch (e: java.lang.Exception) {
                try {
                    mapper.readValue(baseObject, clazz)
                } catch (e: java.lang.Exception) {
                    baseObject //else return String
                }
            }
        }

        private fun findClass(payloadClassName: String?) = try {
            Class.forName("com.auth.app.dto.notification.${payloadClassName?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }}")
        } catch (e: Exception) {
            null
        }
    }
}