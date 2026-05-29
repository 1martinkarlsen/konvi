package com.konvi.exception

import io.ktor.http.HttpStatusCode

open class HttpException(val status: HttpStatusCode, message: String) : Exception(message)

class NotFoundException(message: String = "Not Found") : HttpException(HttpStatusCode.NotFound, message)
class BadRequestException(message: String = "Bad Request") : HttpException(HttpStatusCode.BadRequest, message)
class UnauthorizedException(message: String = "Unauthorized") : HttpException(HttpStatusCode.Unauthorized, message)
class ForbiddenException(message: String = "Forbidden") : HttpException(HttpStatusCode.Forbidden, message)
