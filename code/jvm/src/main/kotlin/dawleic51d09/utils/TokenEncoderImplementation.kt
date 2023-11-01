package dawleic51d09.utils

import dawleic51d09.repository.TokenValidationInfo
import org.springframework.stereotype.Component

@Component
class TokenEncoderImplementation: TokenEncoder {
    override fun createValidationInformation(token: String): TokenValidationInfo {
       return TokenValidationInfo(token)
    }

    override fun validate(validationInfo: TokenValidationInfo, token: String): Boolean {
        return true
    }
}