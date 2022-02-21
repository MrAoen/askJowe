package com.auth.app.dto.notification

import com.fasterxml.jackson.annotation.JsonProperty

data class ChooseSilentNoisePlatforms(

    @JsonProperty("IsNotificationFieldAndroid")
    val isNotificationFieldAndroid: Boolean?,

    @JsonProperty("IsNotificationFieldBrowser")
    val isNotificationFieldBrowser: Boolean?,

    @JsonProperty("IsNotificationFieldIos")
    val isNotificationFieldIos: Boolean
)