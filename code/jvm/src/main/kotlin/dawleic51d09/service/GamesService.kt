package dawleic51d09.service

import dawleic51d09.model.BattleshipLogic
import Either
import dawleic51d09.RealClock
import dawleic51d09.api.models.UserShipPlacement
import dawleic51d09.repository.TransactionManager
import java.time.Duration
import java.util.*
import dawleic51d09.model.*
import org.springframework.stereotype.Component



sealed class GameUpdateError {
    object GameNotFound : GameUpdateError()
    object GameAlreadyStarted :GameUpdateError()
    object GameUserNotFound : GameUpdateError()
    object UserNotInGame: GameUpdateError()
    object GameIsOver: GameUpdateError()
}
sealed class GameGetError {
    object GameNotFound : GameGetError()
    object UserNotInGame : GameGetError()

}


sealed class GameCreationError {
    object GameAlreadyExists : GameCreationError()
}

typealias GameCreationResult = Either<GameCreationError, UUID>
typealias GameGetResult = Either<GameGetError, Game>
typealias GameListGetResult = Either<GameGetError, List<Game>?>
typealias GameUpdateResult = Either<GameUpdateError,Game>
@Component
class GamesService(private val transactionManager: TransactionManager,) {


    fun applyShot(gameId: UUID,userId:Int, position: Position): GameUpdateResult {
        val battleshipGame = BattleshipLogic(RealClock, Duration.ofSeconds(5*60))
        return transactionManager.run {
            val user= it.usersRepository.getUserById(userId) ?: return@run Either.Left(GameUpdateError.GameUserNotFound)
            val gamesRepository = it.gamesRepository
            val game = gamesRepository.getById(gameId) ?: return@run Either.Left(GameUpdateError.GameNotFound)
            if(game.state == Game.State.PLAYER_1_WON || game.state === Game.State.PLAYER_2_WON) return@run Either.Left(GameUpdateError.GameIsOver)
            if(game.player1.id != userId && game.player2.id != userId) return@run Either.Left(GameUpdateError.UserNotInGame)
            else {
                battleshipGame.applyShot(game, Round(position, user))
                gamesRepository.update(battleshipGame.updatedGame)
                if(battleshipGame.updatedGame.state == Game.State.PLAYER_1_WON && battleshipGame.updatedGame.player1.id == userId
                    || battleshipGame.updatedGame.state == Game.State.PLAYER_2_WON && battleshipGame.updatedGame.player2.id == userId){

                    it.usersRepository.updateUser(userId, ++user.score)
                }
                Either.Right(game)
            }
        }
    }

    fun placingPhase(userId: Int,gameId: UUID,shipPlacement: List<UserShipPlacement>): GameUpdateResult{
        val battleshipGame = BattleshipLogic(RealClock, Duration.ofSeconds(5 * 60))
        return transactionManager.run {
            val games = it.gamesRepository
            var game = games.getById(gameId) ?: return@run Either.Left(GameUpdateError.GameNotFound)

            if(game.player1.id != userId && game.player2.id != userId) return@run Either.Left(GameUpdateError.UserNotInGame)
            else{
                if(game.state != Game.State.BOARD_SETUP && game.state != Game.State.ONE_PLAYER_READY) return@run Either.Left(GameUpdateError.GameAlreadyStarted)
                else {
                    shipPlacement.forEach {
                        if (!battleshipGame.placeOnBoard(
                                game,
                                it.ship,
                                it.position,
                                it.direction,
                                userId
                            )
                        ) return@run Either.Left(GameUpdateError.GameNotFound)
                    }
                    battleshipGame.playerReady(game)
                    game = games.update(battleshipGame.updatedGame)
                    Either.Right(game)
                }
            }
        }

    }


    fun create(player1 : User , player2: User): GameCreationResult{
        val battleshipGame = BattleshipLogic(RealClock, Duration.ofSeconds(5))
        return transactionManager.run{
            val newGame=battleshipGame.createNewGame(player1, player2)
            if(newGame == null) return@run Either.Left(GameCreationError.GameAlreadyExists)
            else{
                it.gamesRepository.insert(newGame)
                Either.Right(newGame.id)
            }
        }
    }

    fun getById(userId: Int,id: UUID): GameGetResult{
        return transactionManager.run {
            val game = it.gamesRepository.getById(id) ?: return@run Either.Left(GameGetError.GameNotFound)
            if(game.player1.id != userId && game.player2.id != userId) return@run Either.Left(GameGetError.UserNotInGame)
            else{
                Either.Right(game)
            }
        }
    }

    fun getGamesByUser(username: String):GameListGetResult{
        return transactionManager.run {
            val games = it.gamesRepository.getGamesByUser(username)
            if(games == emptyList<Game>()) return@run Either.Left(GameGetError.GameNotFound)
            else{
                Either.Right(games)
            }
        }
    }



}

