package com.singlesecurekey.app.converters

import com.fasterxml.jackson.databind.JsonNode

import com.fasterxml.jackson.databind.ObjectMapper

import java.io.IOException

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext

import com.fasterxml.jackson.databind.JsonDeserializer


class PayloadDeserializer : JsonDeserializer<String?>() {
    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): String {
        val mapper = jp.codec as ObjectMapper
        val node = mapper.readTree<JsonNode>(jp)
        return mapper.writeValueAsString(node)
    }
}