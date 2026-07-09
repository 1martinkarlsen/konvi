package com.konvi.di

import com.konvi.routing.middleware.AuthMiddleware

interface RouteContext {
    val authMiddleware: AuthMiddleware
}
