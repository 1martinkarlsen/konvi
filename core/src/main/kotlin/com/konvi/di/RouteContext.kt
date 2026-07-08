package com.konvi.di

import com.konvi.routing.middleware.AuthMiddleware

interface RouteContext {
    abstract val authMiddleware: AuthMiddleware
}