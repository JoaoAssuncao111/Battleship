package dawleic51d09.api

import Either
import dawleic51d09.api.models.*
import dawleic51d09.infra.makeSiren
import dawleic51d09.infra.response
import dawleic51d09.model.Game
import dawleic51d09.model.User
import dawleic51d09.repository.PasswordValidationInfo
import dawleic51d09.service.*
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.util.*


@RestController
@CrossOrigin
class UsersController(
    private val userService: UsersService,
    private val gamesService: GamesService
) {
    @PostMapping(Uris.USERS_CREATE)
    fun create(@RequestBody input: UserCreateInputModel): ResponseEntity<*> {
        val res = userService.createUser(input.username, input.password, 0)
        return when (res) {
            is Either.Right -> makeSiren(res.value, "/login").response(201) //ResponseEntity.status(201)
            /*    .header(
                    "Location",
                    Uris.userById(res.value).toASCIIString()
                ).build<Unit>()*/

            is Either.Left -> when (res.value) {
                UserCreationError.InsecurePassword -> Problem.insecurePassword
                UserCreationError.UserAlreadyExists -> Problem.userAlreadyExists
            }

            else -> {
                Problem.unexpectedError
            }
        }
    }

    @PutMapping(Uris.LOGIN)
    fun login(@RequestBody input: UserCreateInputModel): ResponseEntity<*> {
        val res = userService.loginUser(input.username, PasswordValidationInfo(input.password))
        return when (res) {
            is Either.Right -> makeSiren(res.value,"test").response(201)
            is Either.Left -> when (res.value) {
                LoginUserError.InvalidCredentials -> Problem.invalidCredentials
            }
            else -> {
                Problem.unexpectedError
            }
        }
    }


    @PostMapping(Uris.GAME_LOBBY)
    fun joinLobby(user: User): ResponseEntity<*> {
        val res = userService.joinLobby(user.id)
        return when (res) {
            is Either.Right -> makeSiren(res.value,"test").response(201)
            is Either.Left -> when (res.value) {
                UserJoiningError.UserAlredyJoined -> Problem.userAlreadyJoined
                UserJoiningError.UserNotFound -> Problem.userNotFound
            }

            else -> {
                Problem.unexpectedError
            }
        }
    }

    @PostMapping(Uris.USERS_TOKEN)
    fun token(@RequestBody input: UserCreateTokenInputModel, user: User): ResponseEntity<*> {
        val res = userService.createToken(input.username, input.password)
        return when (res) {
            is Either.Right -> ResponseEntity.status(200)
                .body(UserTokenCreateOutputModel(res.value))

            is Either.Left -> when (res.value) {
                TokenCreationError.UserOrPasswordAreInvalid -> Problem.userOrPasswordAreInvalid
            }

            else -> {
                Problem.unexpectedError
            }
        }
    }

    @PutMapping(Uris.GAME_BY_ID)
    fun applyShot(@PathVariable gid: String, user: User, @RequestBody inputModel: ApplyShotInputModel): ResponseEntity<*> {
        val res = gamesService.applyShot(UUID.fromString(gid), user.id, inputModel.position)
        return when (res) {
            is Either.Right -> makeSiren(res.value,"").response(200)

            is Either.Left -> when (res.value) {
                GameUpdateError.GameNotFound -> Problem.gameNotFound
                GameUpdateError.GameAlreadyStarted -> Problem.gameAlreadyStarted
                GameUpdateError.GameUserNotFound -> Problem.userNotFound
                GameUpdateError.UserNotInGame -> Problem.userNotInGame
                GameUpdateError.GameIsOver -> Problem.gameIsOver
            }

            else -> {
                Problem.unexpectedError
            }
        }

    }

    @PutMapping(Uris.PLACING_PHASE)
    fun placingPhase(
        user: User,
        @PathVariable gid: String,
        @RequestBody inputModel: PlacingPhaseInputModel
    ): ResponseEntity<*> {
        val res = gamesService.placingPhase(user.id, UUID.fromString(gid), inputModel.userShips)
        return when (res) {
            is Either.Right -> makeSiren(res.value,"").response(200)

            is Either.Left -> when (res.value) {
                GameUpdateError.GameNotFound -> Problem.gameNotFound
                GameUpdateError.GameAlreadyStarted -> Problem.gameAlreadyStarted
                GameUpdateError.GameUserNotFound -> Problem.userNotFound
                GameUpdateError.UserNotInGame -> Problem.userNotInGame
                GameUpdateError.GameIsOver -> Problem.gameIsOver
            }

            else -> {
                Problem.unexpectedError
            }
        }

    }

    /* ONLY NEEDED IF GAME CREATING FUNCTIONALITY IS ADDED TO THE API
        @PostMapping(Uris.GAMES)
        fun createGame(@RequestBody input: GameCreateInputModel): ResponseEntity<*> {
            val res = gamesService.create(input.player1, input.player2)
            return when (res) {
                is Either.Right -> ResponseEntity.status(201)
                    .header(
                        "Location",
                        Uris.gameById(res.value).toASCIIString()
                    ).build<Unit>()
                is Either.Left -> when (res.value) {
                    GameCreationError.GameAlreadyExists-> Problem.response(400, Problem.gameAlreadyExists)
                }

                else -> {
                    Problem.response(404, Problem.unexpectedError)}
            }
        }

     */
    @GetMapping(Uris.GAME_BY_ID)
    fun getGame(user: User,@PathVariable gid: String): ResponseEntity<*> {
        val res = gamesService.getById(user.id,UUID.fromString(gid))
        return when (res) {
            is Either.Right -> ResponseEntity.status(200).body<Game>(res.value)
            is Either.Left -> when(res.value) {
                GameGetError.UserNotInGame -> Problem.userNotInGame
                GameGetError.GameNotFound -> Problem.gameNotFound
            }
            else -> {
                Problem.unexpectedError
            }
        }

    }
    @GetMapping(Uris.WAIT_STATUS)
    fun checkUserWaitStatus(@PathVariable username: String): ResponseEntity<*>{
        val res = userService.checkUserWaitStatus(username)
        return ResponseEntity.status(200).body<UUID>(res)
    }

    @GetMapping(Uris.USER_GET)
    fun getUserByName(@PathVariable username: String): ResponseEntity<*>{
        val res = userService.getUserByUsername(username)
        return when(res){
            is Either.Right -> ResponseEntity.status(200).body<User>(res.value)
            else -> {
                Problem.userNotFound
            }
        }
    }

    @GetMapping(Uris.USER_HOME)
    fun getUserHome(user: User): UserHomeOutputModel {
        return UserHomeOutputModel(
            id = user.id.toString(),
            username = user.username,

            )
    }

    @GetMapping(Uris.GAMES_BY_USER)
        fun getGamesByUser(user:User): ResponseEntity<*>{
            val res = gamesService.getGamesByUser(user.username)
             return when(res){
            is Either.Right -> ResponseEntity.status(200).body<List<Game>>(res.value)
            else -> {
                Problem.userHasNoGames
            }
        }
    }


    @GetMapping(Uris.RANKING)
    fun getLeaderboard(): MutableList<LeaderBoardOutputModel> {
        val result: MutableList<LeaderBoardOutputModel> = mutableListOf()
        val userList = userService.getLeaderboard()
        userList.forEach { result.add(LeaderBoardOutputModel(it.username, it.score)) }
        return result
    }

  /*  //for docker purposes
    @Controller
    class NewController {
        @GetMapping("/")
        fun redirect() = "redirect:/index.html"
    }*/
}