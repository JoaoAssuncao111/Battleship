package dawleic51d09.model

import java.time.Instant
import java.util.UUID

data class Game(
    val id: UUID,
    var state: State,
    val boardPlayer1: Board,
    val boardPlayer2: Board,
    val created: Instant,
    val updated: Instant,
    var deadline: Instant?,
    val player1: User,
    val player2: User,

    ) {


    enum class State {
        NEXT_PLAYER_1,
        NEXT_PLAYER_2,
        PLAYER_1_WON,
        PLAYER_2_WON,
        BOARD_SETUP,
        ONE_PLAYER_READY;

      /*  val isEnded: Boolean
            get() = this == PLAYER_1_WON || this == PLAYER_2_WON*/
    }

    fun assertGameEquals(game: Game) = boardPlayer1 == game.boardPlayer1
            && boardPlayer2== game.boardPlayer2
            && created== game.created
            && updated==game.updated
            && player1==game.player1
            && player2==game.player2
}