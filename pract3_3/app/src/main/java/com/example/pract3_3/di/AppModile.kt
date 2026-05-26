package com.example.pract3_3.di

import android.content.Context
import com.example.pract3_3.data.local.TokenStorage
import com.example.pract3_3.data.remote.api.DummyJsonApi
import com.example.pract3_3.data.repository.AuthRepositoryImpl
import com.example.pract3_3.data.repository.UsersRepositoryImpl
import com.example.pract3_3.domain.repository.AuthRepository
import com.example.pract3_3.domain.repository.UsersRepository
import com.example.pract3_3.domain.usecase.GetUserByIdUseCase
import com.example.pract3_3.domain.usecase.GetUsersUseCase
import com.example.pract3_3.domain.usecase.LoginUseCase
import com.example.pract3_3.domain.usecase.LogoutUseCase
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

    fun provideAuthRepository(context: Context): AuthRepository {
        val tokenStorage = TokenStorage(context)
        val api = DummyJsonApi(ktorClient)

        return AuthRepositoryImpl(
            api = api,
            tokenStorage = tokenStorage
        )
    }

    fun provideUsersRepository(context: Context): UsersRepository {
        val api = DummyJsonApi(ktorClient)
        val authRepository = provideAuthRepository(context)

        return UsersRepositoryImpl(
            api = api,
            authRepository = authRepository
        )
    }

    fun provideLoginUseCase(context: Context): LoginUseCase {
        return LoginUseCase(
            repository = provideAuthRepository(context)
        )
    }

    fun provideLogoutUseCase(context: Context): LogoutUseCase {
        return LogoutUseCase(
            repository = provideAuthRepository(context)
        )
    }

    fun provideGetUsersUseCase(context: Context): GetUsersUseCase {
        return GetUsersUseCase(
            repository = provideUsersRepository(context)
        )
    }

    fun provideGetUserByIdUseCase(context: Context): GetUserByIdUseCase {
        return GetUserByIdUseCase(
            repository = provideUsersRepository(context)
        )
    }
}