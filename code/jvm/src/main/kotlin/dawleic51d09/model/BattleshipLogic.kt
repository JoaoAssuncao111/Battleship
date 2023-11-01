package dawleic51d09.model

import dawleic51d09.Clock
import java.time.Duration
import java.time.Instant
import java.util.*

class BattleshipLogic(
    private val clock: Clock,
    private val timeout: Duration,
) {

    lateinit var updatedGame: Game

    fun createNewGame(
        player1: User,
        player2: User,
    ): Game {
        val now = clock.now()
        return Game(
            id = UUID.randomUUID(),
            state = Game.State.BOARD_SETUP,//Game.State.NEXT_PLAYER_1,
            boardPlayer1 = Board.create(),
            boardPlayer2 = Board.create(),
            created = now,
            updated = now,
            deadline = now + timeout,
            player1 = player1,
            player2 = player2,
        )
    }


    fun applyShot(
        game: Game,
        round: Round,
    ): ShotOutcome {
        if (round.player.id != game.player2.id && round.player.id != game.player1.id) {
            return ShotOutcome.NotAPlayer
        }
        val now = clock.now()
        return when (game.state) {
            Game.State.PLAYER_1_WON -> ShotOutcome.GameAlreadyEnded
            Game.State.PLAYER_2_WON -> ShotOutcome.GameAlreadyEnded
            Game.State.NEXT_PLAYER_1 -> applyShot(game, round, now, PLAYER_1_LOGIC)
            Game.State.NEXT_PLAYER_2 -> applyShot(game, round, now, PLAYER_2_LOGIC)
            Game.State.BOARD_SETUP -> ShotOutcome.PlayersPlacingShips(game)
            Game.State.ONE_PLAYER_READY -> ShotOutcome.WaitingForOtherPlayer(game)
        }
    }

    fun placeOnBoard(game: Game, ship: Ship, position: Position, direction: Board.Direction, playerId: Int): Boolean {
        val result: Boolean
        return if (game.player1.id == playerId) {
            result = game.boardPlayer1.placeOnBoard(ship, position, direction)
            updatedGame = game
            result
        } else {
            result = game.boardPlayer2.placeOnBoard(ship, position, direction)
            updatedGame = game
            result
        }
    }

    fun playerReady(game: Game) {
        if (game.state == Game.State.BOARD_SETUP) game.state = Game.State.ONE_PLAYER_READY
        else if (game.state == Game.State.ONE_PLAYER_READY) {
            game.state = Game.State.NEXT_PLAYER_1
            game.deadline = Instant.now() + timeout
        }
        updatedGame = game
    }


    fun applyShot(
        game: Game,
        round: Round,
        now: Instant,
        aux: PlayerLogic,
    ): ShotOutcome {
        return if (!aux.isTurn(game, round.player)) {
            ShotOutcome.NotYourTurn

        } else {
            if (now > game.deadline) {
                val newGame = game.copy(state = aux.iLose, deadline = null)
                updatedGame = newGame
                ShotOutcome.Timeout(newGame)
            } else {
                val opponentBoard = if (round.player == game.player1) game.boardPlayer2 else game.boardPlayer1
                val roundTile = opponentBoard.getTile(round.position)
                val newBoard: Board
                if (opponentBoard.canShoot(round.position)) {
                    //check if a ship was hit

                    if (opponentBoard.isShip(round.position)) {
                        /* newBoard = opponentBoard.mutate(round.position, Tile(Content.HIT_SHIP, roundTile.ship))*/
                        newBoard = opponentBoard.setTile(round.position, Tile(Content.HIT_SHIP, roundTile.ship))
                        // ao ser atingido reduz o numero de celulas nao atingidas do navio
                        roundTile.ship!!.shipTiles--

                        if (opponentBoard.isShipDown(roundTile.ship)) {
                            opponentBoard.sinkShip(roundTile)

                            if (opponentBoard.hasWon()) {

                                if(game.player1.id == round.player.id) game.player1.score++ else game.player2.score++
                                val newGame = newGameBoard(game, opponentBoard, aux.iWon, newBoard, null)
                                updatedGame = newGame
                                return ShotOutcome.YouWon(newGame)
                            }
                        }
                        //caso acerte numa ship mas nao tenha afundado
                        val newGame = newGameBoard(game, opponentBoard, aux.iKeepTurn, newBoard, now + timeout)
                        updatedGame = newGame
                        ShotOutcome.KeepTurn(newGame)

                    } else {
                        //quando acerta na agua
                        newBoard = opponentBoard.mutate(round.position, Tile(Content.HIT_WATER, null))
                        val newGame = newGameBoard(game, opponentBoard, aux.nextPlayer, newBoard, now + timeout)
                        updatedGame = newGame
                        ShotOutcome.OthersTurn(newGame)
                    }
                } else {
                    ShotOutcome.InvalidPosition
                }
            }
        }
    }


    private fun newGameBoard(g: Game, board: Board, state: Game.State, newBoard: Board, deadline: Instant?): Game {
        return if (board.equals(g.boardPlayer1)) {//board===g.boardPlayer1 , equals so para utilizar o equals do board verificar se e suposto
            g.copy(
                boardPlayer1 = newBoard,
                state = state,
                deadline = deadline,
            )
        } else {
            g.copy(
                boardPlayer2 = newBoard,
                state = state,
                deadline = deadline,
            )
        }
    }

    class PlayerLogic(
        val isTurn: (game: Game, user: User) -> Boolean,
        val iLose: Game.State,
        val iWon: Game.State,
        val nextPlayer: Game.State,
        val iKeepTurn: Game.State
    )

    companion object {
        private val PLAYER_1_LOGIC = PlayerLogic(
            isTurn = { game, user -> game.isPlayer1(user) },
            iLose = Game.State.PLAYER_2_WON,
            iWon = Game.State.PLAYER_1_WON,
            nextPlayer = Game.State.NEXT_PLAYER_2,
            iKeepTurn = Game.State.NEXT_PLAYER_1
        )
        private val PLAYER_2_LOGIC = PlayerLogic(
            isTurn = { game, user -> game.isPlayer2(user) },
            iLose = Game.State.PLAYER_1_WON,
            iWon = Game.State.PLAYER_2_WON,
            nextPlayer = Game.State.NEXT_PLAYER_1,
            iKeepTurn = Game.State.NEXT_PLAYER_2
        )
    }

    fun getWinner(game: Game) = if (game.state == Game.State.PLAYER_1_WON) game.player1 else game.player2
}


//Every possible round outcome
sealed class ShotOutcome {
    object NotYourTurn : ShotOutcome()
    object GameAlreadyEnded : ShotOutcome()
    object NotAPlayer : ShotOutcome() //?
    object InvalidPosition : ShotOutcome()
    data class Timeout(val game: Game) : ShotOutcome()
    data class YouWon(val game: Game) : ShotOutcome()
    data class OthersTurn(val game: Game) : ShotOutcome()
    data class WaitingForOtherPlayer(val game: Game) : ShotOutcome()
    data class PlayersPlacingShips(val game: Game) : ShotOutcome()

    //Used when a shot hits a ship so the player keeps his turn
    data class KeepTurn(val game: Game) : ShotOutcome()
}

private fun Game.isPlayer1(player: User) = this.player1.id == player.id
private fun Game.isPlayer2(player: User) = this.player2.id == player.id



