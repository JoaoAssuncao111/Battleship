package dawleic51d09.model

import dawleic51d09.repository.PasswordValidationInfo

data class User(
    val id: Int,
    val username: String,
    val passwordValidation: PasswordValidationInfo,
    var score: Int
)