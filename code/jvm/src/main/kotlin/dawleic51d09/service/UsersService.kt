package dawleic51d09.service

import Either
import dawleic51d09.model.Game
import dawleic51d09.repository.PasswordValidationInfo
import org.springframework.security.crypto.password.PasswordEncoder
import dawleic51d09.model.User
import dawleic51d09.model.UserLogic
import dawleic51d09.repository.TransactionManager
import dawleic51d09.utils.TokenEncoder
import org.springframework.stereotype.Component
import java.util.UUID

sealed class UserCreationError {
    object UserAlreadyExists : UserCreationError()
    object InsecurePassword : UserCreationError()
}

sealed class UserJoiningError {
    object UserAlredyJoined : UserJoiningError()
    object UserNotFound : UserJoiningError()
}

sealed class UserRemoveFromLobbyError {
    object UserAlredyRemoved : UserRemoveFromLobbyError()
}

sealed class UserUpdateError {
    object UserNotFound : UserUpdateError()
}

sealed class UserGetByIdError {
    object UserNotFound : UserGetByIdError()
}

sealed class TokenCreationError {
    object UserOrPasswordAreInvalid : TokenCreationError()
}

sealed class LoginUserError {
    object InvalidCredentials : LoginUserError()
}


typealias UserCreationResult = Either<UserCreationError, String>
typealias LoginUserResult = Either<LoginUserError, Pair<String?, String>>
typealias UserJoiningResult = Either<UserJoiningError, Game?>
typealias UserRemoveFromLobbyResult = Either<UserRemoveFromLobbyError, Int>
typealias UserUpdateResult = Either<UserUpdateError, Int>
typealias UserGetResult = Either<UserGetByIdError, User>


typealias TokenCreationResult = Either<TokenCreationError, String>


@Component
class UsersService(
    private val transactionManager: TransactionManager,
    private val userLogic: UserLogic,
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
) {

    fun loginUser(username: String, password: PasswordValidationInfo): LoginUserResult {
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val res = usersRepository.loginUser(username, password)
            if (res?.first == null) return@run Either.Left(LoginUserError.InvalidCredentials)
            else {
                Either.Right(res)
            }

        }
    }

    fun checkUserWaitStatus(username: String): UUID? {
        return transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.checkUserWaitStatus(username)
        }
    }

    fun createUser(username: String, password: String, score: Int): UserCreationResult {

        if (!userLogic.isSafePassword(password)) {
            return Either.Left(UserCreationError.InsecurePassword)
        }

        val passwordValidationInfo = PasswordValidationInfo(
            passwordEncoder.encode(password)
        )

        return transactionManager.run {
            val usersRepository = it.usersRepository
            if (usersRepository.isUserStoredByUsername(username)) {
                Either.Left(UserCreationError.UserAlreadyExists)
            } else {
                val id = usersRepository.storeUser(username, passwordValidationInfo, score)
                createToken(username, password)
                Either.Right(id)
            }
        }
    }

    fun createToken(username: String, password: String): TokenCreationResult {
        if (username.isBlank() || password.isBlank()) {
            Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val user: User = usersRepository.getUserByUsername(username) ?: return@run userNotFound()
            if (!passwordEncoder.matches(password, user.passwordValidation.validationInfo)) {
                return@run Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
            }
            val token = userLogic.generateToken()
            usersRepository.createToken(user.id, tokenEncoder.createValidationInformation(token))
            Either.Right(token)
        }
    }

    fun getUserByToken(token: String): User? {
        if (!userLogic.canBeToken(token)) {
            return null
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val tokenValidationInfo = tokenEncoder.createValidationInformation(token)
            usersRepository.getUserByTokenValidationInfo(tokenValidationInfo)
        }
    }

    private fun userNotFound(): TokenCreationResult {
        passwordEncoder.encode("changeit")
        return Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
    }

    fun joinLobby(userId: Int): UserJoiningResult {
        return transactionManager.run {
            val usersRepository = it.usersRepository
            if (usersRepository.isUserInLobby(userId)) return@run Either.Left(UserJoiningError.UserAlredyJoined)
            else if (usersRepository.getUserById(userId) == null) return@run Either.Left(UserJoiningError.UserNotFound)
            else {
                val game = usersRepository.joinLobby(userId)
                Either.Right(game)
            }
        }

    }

    fun removeFromLobby(userId: Int/*token:Token*/): UserRemoveFromLobbyResult {
        return transactionManager.run {
            val usersRepository = it.usersRepository
            if (usersRepository.isUserInLobby(userId)) return@run Either.Left(UserRemoveFromLobbyError.UserAlredyRemoved)
            else {
                usersRepository.removeFromLobby(userId)
                Either.Right(userId)
            }
        }
    }


    /*fun getUserById(userId: Int): UserGetResult {
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val user = usersRepository.getUserByUsername(userId)
            if(user == null) return@run Either.Left(UserGetByIdError.UserNotFound)
            else{ Either.Right(User(user.id,user.username,user.passwordValidation,user.score))}
        }
    }
    */

    fun getUserByUsername(username: String): UserGetResult {
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val user = usersRepository.getUserByUsername(username)
            if (user == null) return@run Either.Left(UserGetByIdError.UserNotFound)
            else {
                Either.Right(User(user.id, user.username, user.passwordValidation, user.score))
            }
        }
    }

    fun getLeaderboard(): List<User> {
        return transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.getLeaderBoard()
        }
    }
}

