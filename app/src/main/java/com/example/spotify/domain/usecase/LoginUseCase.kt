package com.example.spotify.domain.usecase

import com.example.spotify.domain.repository.AuthRepository

class LoginUseCase (private val authRepository: AuthRepository) {
    operator fun invoke(email: String, password: String): Result<Unit> {
        return authRepository.login(email, password)
    }
}