package com.example.pract3_2.di

import com.example.pract3_2.data.remote.api.NobelApi
import com.example.pract3_2.data.repository.NobelRepositoryImpl
import com.example.pract3_2.domain.repository.NobelRepository
import com.example.pract3_2.domain.usecase.AddFavoritePrizeUseCase
import com.example.pract3_2.domain.usecase.GetFavoriteNobelLaureatesUseCase
import com.example.pract3_2.domain.usecase.GetNobelLaureatesUseCase
import com.example.pract3_2.domain.usecase.LoginUseCase
import com.example.pract3_2.domain.usecase.RemoveFavoritePrizeUseCase
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object AppModule {

    private val ktorClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            )
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }

    private val nobelApi: NobelApi =
        NobelApi(ktorClient)

    private val nobelRepository: NobelRepository =
        NobelRepositoryImpl(nobelApi)

    val getNobelLaureatesUseCase: GetNobelLaureatesUseCase =
        GetNobelLaureatesUseCase(nobelRepository)

    val loginUseCase: LoginUseCase =
        LoginUseCase(nobelRepository)

    val getFavoriteNobelLaureatesUseCase: GetFavoriteNobelLaureatesUseCase =
        GetFavoriteNobelLaureatesUseCase(nobelRepository)

    val addFavoritePrizeUseCase: AddFavoritePrizeUseCase =
        AddFavoritePrizeUseCase(nobelRepository)

    val removeFavoritePrizeUseCase: RemoveFavoritePrizeUseCase =
        RemoveFavoritePrizeUseCase(nobelRepository)
}
