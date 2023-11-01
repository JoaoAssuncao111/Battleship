package dawleic51d09.repository

import dawleic51d09.model.User
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import dawleic51d09.repository.jdbi.JdbiUsersRepository
import dawleic51d09.repository.jdbi.configure
import kotlin.test.assertEquals

class JdbiUserRepositoryTest {


    private val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")

    private val jdbi = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL(jdbcDatabaseURL)
        }
    ).configure()

    private fun testWithHandleAndRollback(block: (Handle) -> Unit) = jdbi.useTransaction<Exception> { handle ->
        block(handle)
        handle.rollback()
    }

    @Test
    fun joinLobbyTest(): Unit = testWithHandleAndRollback { handle: Handle ->

        // given: repositories and logic
        val userRepo = JdbiUsersRepository(handle)

        userRepo.storeUser("alice", PasswordValidationInfo(""),0)
        val alice =userRepo.getUserByUsername("alice")

        var lobbysize=handle.createQuery("""select COUNT(*) from lobby """).mapTo<Int>().one().toInt()

        assertEquals(lobbysize,0)

        //first case, only one user inside the lobby
        userRepo.joinLobby(alice!!.id)

        lobbysize=handle.createQuery("""select COUNT(*) from lobby """).mapTo<Int>().one().toInt()

        val user1 = handle.createQuery(
            """ select u.id,u.username,u.password_validation,u.score
             from lobby join users u on lobby.user_id = u.id"""
        ).mapTo<User>().singleOrNull()

        assertEquals(lobbysize,1)
        assertEquals(user1!!.id,alice.id)


        userRepo.storeUser("bob", PasswordValidationInfo(""),0)
        val bob = userRepo.getUserByUsername("bob")

        var gameCount=handle.createQuery("""select COUNT(*) from games """).mapTo<Int>().one().toInt()

        assertEquals(gameCount,0)

        //second case where a second player joins the lobby, it removes the player that was already there and starts a new game with both
        userRepo.joinLobby(bob!!.id)

        lobbysize=handle.createQuery("""select COUNT(*) from lobby """).mapTo<Int>().one().toInt()

        //alice was removed from the lobby
        assertEquals(lobbysize,0)

        gameCount=handle.createQuery("""select COUNT(*) from games """).mapTo<Int>().one().toInt()

        //a game was created with alice and bob
        assertEquals(gameCount,1)

    }
}