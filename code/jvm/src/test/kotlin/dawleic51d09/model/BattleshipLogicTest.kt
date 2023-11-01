package dawleic51d09.model

import dawleic51d09.Clock
import dawleic51d09.RealClock
import dawleic51d09.repository.PasswordValidationInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.time.Duration
import java.time.Instant

class BattleshipLogicTest {

    @Test
    fun `simple game and bob wins`() {//done
        // given:a game
        var game = gameLogic.createNewGame(alice, bob)


        gameLogic.placeOnBoard(game,Ship(0,"Submarine"),Position(0,0), Board.Direction.V,game.player1.id)
        gameLogic.playerReady(game)
        gameLogic.placeOnBoard(game,Ship(0,"Submarine"),Position(0,0), Board.Direction.H,game.player2.id)
        gameLogic.playerReady(game)

        // when: alice plays
        var result = gameLogic.applyShot(game, Round(Position(0, 0), alice))

        // then: alice keeps playing
        game = when (result) {
            is ShotOutcome.KeepTurn -> result.game
            else -> fail("Unexpected round result $result")
        }
        assertEquals(Game.State.NEXT_PLAYER_1, game.state)

        // when: alice keeps playing
        result = gameLogic.applyShot(game, Round(Position(1, 0), alice))

        // alice misses
        game = when (result) {
            is ShotOutcome.OthersTurn -> result.game
            else -> fail("Unexpected round result $result")
        }
        assertEquals(Game.State.NEXT_PLAYER_2, game.state)

        // when: bob plays
        result = gameLogic.applyShot(game, Round(Position(0, 0), bob))

        // then: bob hits
        game = when (result) {
            is ShotOutcome.KeepTurn -> result.game
            else -> fail("Unexpected round result $result")
        }
        assertEquals(Game.State.NEXT_PLAYER_2, game.state)

        // when: bob plays again
        result = gameLogic.applyShot(game, Round(Position(1, 0), bob))

        // then: bob won
        game = when (result) {
            is ShotOutcome.YouWon -> result.game
            else -> fail("Unexpected round result $result")
        }
        assertEquals(Game.State.PLAYER_2_WON, game.state)
        
    }

    @Test
    fun `InvalidTurnTest`() {//done
        // given: a game
        var game = gameLogic.createNewGame(alice, bob)

        gameLogic.placeOnBoard(game,Ship(0,"Submarine"),Position(0,0), Board.Direction.V,game.player1.id)
        gameLogic.playerReady(game)
        gameLogic.placeOnBoard(game,Ship(0,"Submarine"),Position(0,0), Board.Direction.H,game.player2.id)
        gameLogic.playerReady(game)

        // when: alice plays
        var result = gameLogic.applyShot(game, Round(Position(1, 1), alice))

        // then: next player is bob
        game = when (result) {
            is ShotOutcome.OthersTurn -> result.game
            else -> fail("Unexpected round result $result")
        }
        assertEquals(Game.State.NEXT_PLAYER_2, game.state)

        // when: bob plays
        result = gameLogic.applyShot(game, Round(Position(1, 1), bob))

        // then: next player is alice
        game = when (result) {
            is ShotOutcome.OthersTurn -> result.game
            else -> fail("Unexpected round result $result")
        }
        assertEquals(Game.State.NEXT_PLAYER_1, game.state)

        // when: bob plays
        result = gameLogic.applyShot(game, Round(Position(1, 0), bob))

        // then: result is a failure and next player is still alice
        when (result) {
            is ShotOutcome.NotYourTurn -> {}
            else -> fail("Unexpected round result $result")
        }
        assertEquals(Game.State.NEXT_PLAYER_1, game.state)
    }

    @Test
    fun `alice wins`() {//done
        // given: a game and a list of rounds
        val game = gameLogic.createNewGame(alice, bob)

        gameLogic.placeOnBoard(game,Ship(0,"Submarine"),Position(0,0), Board.Direction.V,game.player1.id)
        gameLogic.playerReady(game)
        gameLogic.placeOnBoard(game,Ship(0,"Submarine"),Position(0,0), Board.Direction.H,game.player2.id)
        gameLogic.playerReady(game)

        val rounds = listOf(
            Round(Position(0, 1), alice),//hit
            Round(Position(1, 1), alice),//misses

            Round(Position(1, 0), bob),//hit
            Round(Position(1, 1), bob),//misses

            Round(Position(1, 0), alice),//misses

            Round(Position(0, 1), bob),//misses

            Round(Position(0, 0), alice),//hit and win
        )

        // when: the rounds are applied
        val result = play(gameLogic, game, rounds)

        // then: alice wins
        when (result) {
            is ShotOutcome.YouWon -> {
                assertEquals(Game.State.PLAYER_1_WON, result.game.state)
            }

            else -> fail("Unexpected round result $result")
        }
    }

    @Test
    fun `timeout test`() {//done
        // given: a game logic, a game and a list of rounds
        val testClock = TestClock()
        val timeout = Duration.ofMinutes(5)
        val gameLogic = BattleshipLogic(testClock, timeout)
        var game = gameLogic.createNewGame(alice, bob)

        Companion.gameLogic.placeOnBoard(game,Ship(0,"Submarine"),Position(0,0), Board.Direction.V,game.player1.id)
        Companion.gameLogic.playerReady(game)
        Companion.gameLogic.placeOnBoard(game,Ship(0,"Submarine"),Position(0,0), Board.Direction.H,game.player2.id)
        Companion.gameLogic.playerReady(game)
        // when: alice plays
        testClock.advance(timeout.minusMinutes(1))
        var result = gameLogic.applyShot(game, Round(Position(1, 1), alice))

        // then: round is accepted
        game = when (result) {
            is ShotOutcome.OthersTurn -> result.game
            else -> fail("Unexpected result $result")
        }

        // when: bob plays
        testClock.advance(timeout.plusSeconds(1))
        result = gameLogic.applyShot(game, Round(Position(1, 1), bob))

        // then: round is not accepted and alice won
        game = when (result) {
            is ShotOutcome.Timeout -> result.game
            else -> fail("Unexpected result $result")
        }
        assertEquals(Game.State.PLAYER_1_WON, game.state)
    }

    private fun play(logic: BattleshipLogic, initialGame: Game, rounds: List<Round>): ShotOutcome? {
        var previousResult: ShotOutcome? = null
        for (round in rounds) {
            val game = when (previousResult) {
                null -> initialGame
                is ShotOutcome.OthersTurn -> previousResult.game
                is ShotOutcome.KeepTurn -> previousResult.game
                else -> fail("Unexpected round result $previousResult")
            }
            previousResult = logic.applyShot(game, round)
        }
        return previousResult
    }

    companion object {
        private val gameLogic = BattleshipLogic(
            RealClock,
            Duration.ofMinutes(5)
        )

        // our test players
        private val alice = User(1, "alice", PasswordValidationInfo(""),0)
        private val bob = User(2, "bob", PasswordValidationInfo(""),0)
    }

    class TestClock : Clock {

        private var now = Instant.ofEpochSecond(0)

        override fun now(): Instant = now

        fun advance(duration: Duration) {
            now += duration
        }
    }
}