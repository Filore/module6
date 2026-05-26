package com.example.nobelserver

import com.example.nobelserver.di.AppDependencies
import com.example.nobelserver.plugins.configureHTTP
import com.example.nobelserver.plugins.configureMonitoring
import com.example.nobelserver.plugins.configureSecurity
import com.example.nobelserver.plugins.configureSerialization
import com.example.nobelserver.routing.configureAuthRoutes
import com.example.nobelserver.routing.configureDocsRoutes
import com.example.nobelserver.routing.configurePrizeRoutes
import com.example.nobelserver.routing.configureUserRoutes
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    val dependencies = AppDependencies.create()

    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureSecurity(dependencies.jwtService)
    configureDocsRoutes()
    configureAuthRoutes(dependencies.authService)
    configurePrizeRoutes(dependencies.prizeUseCase)
    configureUserRoutes(dependencies.authService, dependencies.favoritePrizesUseCase)
}

