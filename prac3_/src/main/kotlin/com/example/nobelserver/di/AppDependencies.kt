package com.example.nobelserver.di

import com.example.nobelserver.data.database.DatabaseFactory
import com.example.nobelserver.data.repository.PostgresFavoritePrizeRepository
import com.example.nobelserver.data.repository.PostgresNobelPrizeRepository
import com.example.nobelserver.data.repository.PostgresUserRepository
import com.example.nobelserver.domain.usecase.GetNobelPrizesUseCase
import com.example.nobelserver.domain.usecase.ManageFavoritePrizesUseCase
import com.example.nobelserver.security.JwtService
import com.example.nobelserver.service.AuthService

data class AppDependencies(
    val jwtService: JwtService,
    val authService: AuthService,
    val prizeUseCase: GetNobelPrizesUseCase,
    val favoritePrizesUseCase: ManageFavoritePrizesUseCase
) {
    companion object {
        fun create(): AppDependencies {
            DatabaseFactory.init()

            val jwtService = JwtService()
            val userRepository = PostgresUserRepository()
            val authService = AuthService(jwtService, userRepository)
            val prizeRepository = PostgresNobelPrizeRepository()
            val prizeUseCase = GetNobelPrizesUseCase(prizeRepository)
            val favoritePrizesUseCase = ManageFavoritePrizesUseCase(
                PostgresFavoritePrizeRepository(prizeRepository)
            )

            return AppDependencies(
                jwtService = jwtService,
                authService = authService,
                prizeUseCase = prizeUseCase,
                favoritePrizesUseCase = favoritePrizesUseCase
            )
        }
    }
}
