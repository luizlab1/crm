package com.example.crm.controller

import io.swagger.v3.oas.annotations.media.Schema

data class GoogleAuthRequest(
    @field:Schema(example = "google_id_token")
    val credential: String
)
