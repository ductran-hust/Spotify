package com.example.spotify.domain.repository

interface AuthRepository {
    fun login(email: String, password: String): Result<Unit>
}