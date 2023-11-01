package dawleic51d09.api

import org.springframework.http.ResponseEntity
import java.net.URI

class Problem(
    errorMessage : String
) {
    var errorMessage = errorMessage
    companion object {

        const val MEDIA_TYPE = "application/problem+json"
        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body("$status " + problem.errorMessage)

        val userAlreadyExists = response(403, Problem("User Already Exists"))

        val insecurePassword = response(400,Problem("Insecure Password"))

        val userOrPasswordAreInvalid =response(400, Problem("User or Password are Invalid"))

        val userAlreadyJoined = response(403, Problem("User Already Joined"))

        val gameNotFound = response(404,Problem("Game not found"))

        val userNotFound= response(404, Problem("User not found"))

        val unexpectedError = response(400,Problem("Unexpected Error"))

        val gameAlreadyExists= response(403,Problem("Game Already Started"))

        val gameAlreadyStarted= response(400, Problem("Game already started"))

        val userNotInGame= response(400,Problem("User not in game"))

        val invalidCredentials = response(400,Problem("Invalid Credentials"))

        val gameIsOver = response(400,Problem("Game has already ended"))

        val userHasNoGames = response(400,Problem("User has no Games"))
    }


}