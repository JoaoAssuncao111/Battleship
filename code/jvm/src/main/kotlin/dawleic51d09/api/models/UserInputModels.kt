package dawleic51d09.api.models

import dawleic51d09.model.*

data class UserCreateInputModel(
    val username: String,
    val password: String,
)

data class UserCreateTokenInputModel(
    val username: String,
    val password: String,
)

data class ApplyShotInputModel(
    val position: Position,
)

data class UserShipPlacement(
    val position: Position,
    val ship: Ship,
    val direction: Board.Direction
)

class PlacingPhaseInputModel(

    val userShips: MutableList<UserShipPlacement> = mutableListOf()

)


data class GameCreateInputModel(
    val player1: User,
    val player2: User,
)

data class UserGetByIdInputModel(
    val userId: Int
)