package dawleic51d09.repository

import dawleic51d09.model.BattleshipLogic
import dawleic51d09.RealClock
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import dawleic51d09.repository.jdbi.JdbiGamesRepository
import dawleic51d09.repository.jdbi.JdbiUsersRepository
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import dawleic51d09.repository.jdbi.configure
import java.time.Duration


class GameRepositoryTests {

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
    fun `can create and retrieve`(): Unit = testWithHandleAndRollback { handle: Handle ->

        // given: repositories and logic
        val userRepo = JdbiUsersRepository(handle)
        val gameRepo = JdbiGamesRepository(handle)
        val gameLogic = BattleshipLogic(RealClock, Duration.ofMinutes(5))

        // and: two existing users
        userRepo.storeUser("alice", PasswordValidationInfo(""),0)
        userRepo.storeUser("bob", PasswordValidationInfo(""),0)

        // when: creating and inserting a game
        val alice = userRepo.getUserByUsername("alice") ?: fail("user must exist")
        val bob = userRepo.getUserByUsername("bob") ?: fail("user must exist")
        val game = gameLogic.createNewGame(alice, bob)
        gameRepo.insert(game)

        // and: retrieving the game
        val retrievedGame = gameRepo.getById(game.id)

        // then: the two games are equal
        //assertEquals(game, retrievedGame)
        game.assertGameEquals(retrievedGame!!)
    }
}