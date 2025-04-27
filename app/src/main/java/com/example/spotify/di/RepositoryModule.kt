package com.example.spotify.di

import com.example.spotify.data.repository.AuthRepositoryImpl
import com.example.spotify.domain.repository.AuthRepository

object RepositoryModule {
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }
}