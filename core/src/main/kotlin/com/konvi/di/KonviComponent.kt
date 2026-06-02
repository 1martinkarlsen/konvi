package com.konvi.di

import com.konvi.routing.middleware.AuthMiddleware

abstract class KonviComponent {
    abstract val authMiddleware: AuthMiddleware
}
