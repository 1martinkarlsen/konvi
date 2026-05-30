package com.konvi.exception

import kotlinx.serialization.Serializable

@Serializable
internal data class ErrorResponse(val status: Int, val message: String)
