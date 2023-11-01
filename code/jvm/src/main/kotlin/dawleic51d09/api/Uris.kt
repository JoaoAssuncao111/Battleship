package dawleic51d09.api

import dawleic51d09.model.Game
import org.springframework.web.util.UriTemplate
import java.net.URI
import java.util.UUID

object Uris {

    const val HOME = "/home"
    const val USER_HOME = "/users/home"
    const val LOGIN ="/login"
    const val GAME_BY_ID = "/games/{gid}"
    const val PLACING_PHASE= "/games/{gid}/setup"
    const val RANKING ="/leaderboard"
    const val GAME_LOBBY="/lobby"
    const val WAIT_STATUS="/waitstatus/{username}"
    const val USERS_CREATE = "/register"
    const val USERS_TOKEN = "/users/token"
    const val USER_GET = "/users/{username}"
    const val GAMES_BY_USER = "/usergames"

    fun home(): URI = URI(HOME)
    //fun gameById(game: Game) = UriTemplate(GAME_BY_ID).expand(game.id)

   //fun createUser(): URI = URI("/register")

    //fun getLeaderboard():URI=URI(RANKING)
    //fun gameById(id:UUID)= UriTemplate(GAMES).expand(id)
}