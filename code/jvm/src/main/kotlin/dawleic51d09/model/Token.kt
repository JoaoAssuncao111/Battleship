package dawleic51d09.model

import dawleic51d09.repository.TokenValidationInfo
import java.time.Instant

class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: Int,
    val createdAt: Instant,
    val lastUsedAt: Instant
)