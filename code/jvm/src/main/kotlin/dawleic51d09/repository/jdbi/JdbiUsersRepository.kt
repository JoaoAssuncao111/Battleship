package dawleic51d09.repository.jdbi

import dawleic51d09.model.*
import dawleic51d09.repository.PasswordValidationInfo
import dawleic51d09.repository.TokenValidationInfo
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import dawleic51d09.repository.UsersRepository
import java.time.Instant
import java.util.*


class JdbiUsersRepository(
    private val handle: Handle
) : UsersRepository {

    val LEADERBOARD_LIMIT = 10

    override fun getUserByUsername(username: String): User? =
        handle.createQuery("select * from Users where username = :username")
            .bind("username", username)
            .mapTo(User::class.java)
            .singleOrNull()

    override fun getUserById(id: Int): User? =
        handle.createQuery("""select * from users where id = :id""").bind("id", id).mapTo<User>().singleOrNull()

    override fun isUserInLobby(id: Int): Boolean =
        handle.createQuery("""SELECT COUNT(*) FROM lobby WHERE user_id = :id """)
            .bind("id", id)
            .mapTo<Int>()
            .single() == 1

    override fun loginUser(username: String, passwordValidation: PasswordValidationInfo): Pair<String?, String>? {
        if (!isUserStoredByUsername(username)) return null
        val userId = getUserByUsername(username)!!.id
        val password = passwordValidation.validationInfo
        val token = handle.createQuery(
            """SELECT token_validation FROM tokens 
            join users u on u.id = tokens.user_id
             WHERE user_id = :userId 
            and u.password_validation=:password and u.username = :username """.trimMargin()
        )
            .bind("userId", userId)
            .bind("password", password)
            .bind("username", username)
            .mapTo<String>()
            .singleOrNull()
        return Pair(token, username)

    }

    override fun storeUser(username: String, passwordValidation: PasswordValidationInfo, score: Int): String =
        if (isUserStoredByUsername(username)) "UserAlreadyStored"
        else
            handle.createUpdate(
                """
            insert into Users (username, password_validation,score) values (:username, :password_validation,:score)
            """
            )
                .bind("username", username)
                .bind("password_validation", passwordValidation.validationInfo)
                .bind("score", score)
                .executeAndReturnGeneratedKeys()
                .mapTo<Int>()
                .one()
                .toString()


    override fun isUserStoredByUsername(username: String): Boolean =
        handle.createQuery("select count(*) from Users where username = :username")
            .bind("username", username)
            .mapTo<Int>()
            .single() == 1

    override fun createToken(userId: Int, token: TokenValidationInfo) {
        handle.createUpdate("insert into Tokens(user_id, token_validation) values (:user_id, :token_validation)")
            .bind("user_id", userId)
            .bind("token_validation", token.validationInfo)
            .execute()
    }

    override fun updateTokenLastUsed(token: Token, now: Instant) {
        /*handle.createUpdate(
            """
                update Tokens
                set last_used_at = :last_used_at
                where token_validation = :validation_information
            """.trimIndent()
        )
            .bind("last_used_at", now.epochSecond)
            .bind("validation_information", token.tokenValidationInfo.validationInfo)
            .execute()

         */
    }


    override fun getUserByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): User? =
        handle.createQuery(
            """
            select id, username, password_validation, score
            from Users as users 
            inner join Tokens as tokens 
            on users.id = tokens.user_id
            where token_validation = :validation_information
            """
        )
            .bind("validation_information", tokenValidationInfo.validationInfo)
            .mapTo<User>()
            .singleOrNull()


    override fun joinLobby(userId: Int/*,username: String*/): Game? {
        var game: Game? = null
        handle.createUpdate("""set transaction isolation level serializable;""")//serializable para garantir que nao ocorre
        //concorrencia de inserçoes na tabela

        val lobbySize = handle.createQuery(""" select COUNT(*) from lobby """).mapTo<Int>().one().toInt()

        val username = getUserById(userId)!!.username

        if (lobbySize == 0) {
            handle.createUpdate("""insert into Lobby(user_id,lobby_username) values(:userId,:username) """.trimMargin())
                .bind("userId", userId)
                .bind("username", username).execute()
            handle.createUpdate("insert into waitstatus values (:username,null)")
                .bind("username", username)
                .execute()
        } else {
            val user1 = handle.createQuery(
                """ select u.id,u.username,u.password_validation,u.score
             from lobby join users u on lobby.user_id = u.id"""
            ).mapTo<User>().singleOrNull()

            removeFromLobby(user1!!.id)

            val user2 = getUserByUsername(username)

            game = Game(
                UUID.randomUUID(), Game.State.BOARD_SETUP, Board.create(), Board.create(), Instant.now(),
                Instant.now(), null, user1, user2!!
            )

            JdbiGamesRepository(handle).insert(game)
            handle.createUpdate("update waitstatus set game_id = :game_id where username = :username")
                .bind("game_id", game.id)
                .bind("username", user1.username)
                .execute()

        }
        return game
    }


    override fun removeFromLobby(userId: Int) {
        handle.createUpdate("""delete from Lobby where user_id= :userId """).bind("userId", userId).execute()
    }


    override fun updateUser(userId: Int, score: Int) {
        handle.createUpdate(""" update users set score = :score where id= :userId""")
            .bind("score", score)
            .bind("userId", userId)
            .execute()
    }

    override fun getLeaderBoard(): List<User> =
        handle.createQuery(""" select * from users order by score desc LIMIT $LEADERBOARD_LIMIT""").mapTo<User>()
            .toList()

    override fun checkUserWaitStatus(username: String): UUID? {
       val gameId =  handle.createQuery("select game_id from waitstatus where username = :username")
            .bind("username", username)
            .mapTo<UUID>()
            .singleOrNull()
        if(gameId != null) handle.createUpdate("delete from waitstatus where username = :username")
            .bind("username", username)
            .execute()
        return gameId
    }
}

