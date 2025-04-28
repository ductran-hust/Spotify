package com.example.spotify.data.repository

import com.example.spotify.domain.repository.AuthRepository

class AuthRepositoryImpl: AuthRepository {
    override fun login(email: String, password: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}