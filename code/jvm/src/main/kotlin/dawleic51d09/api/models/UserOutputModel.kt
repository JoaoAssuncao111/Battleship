package dawleic51d09.api.models


import dawleic51d09.model.Game
import dawleic51d09.model.User
import java.util.UUID

class UserTokenCreateOutputModel(
    val token: String
)

class ApplyShotOutputModel(
    val game: Game
)

class PlacingPhaseOutputModel(
    val game: Game
)
class UserHomeOutputModel(
    val id: String,
    val username: String,
)

class UserGamesOutputModel(
    val games: List<Game>
)

class UserGetByIdOutputModel(
   val user: User
)