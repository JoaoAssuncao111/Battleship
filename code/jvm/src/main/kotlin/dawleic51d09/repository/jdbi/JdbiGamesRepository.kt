package dawleic51d09.repository.jdbi

import org.jdbi.v3.core.kotlin.mapTo
import dawleic51d09.model.*
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.mapper.Nested
import dawleic51d09.repository.GamesRepository
import java.time.Instant
import java.util.*

class JdbiGamesRepository(
    private val handle: Handle,
) : GamesRepository {


    override fun insert(game: Game) {
        handle.createUpdate(
            """
           insert into Games(id, state, boardPlayer1,boardPlayer2, created, updated, deadline, player1, player2) 
            values(:id, :state, :boardPlayer1,:boardPlayer2, :created, :updated, :deadline, :player1, :player2)
       """
        )
            .bind("id", game.id)
            .bind("state", game.state)
            .bind("boardPlayer1", game.boardPlayer1.toString())
            .bind("boardPlayer2", game.boardPlayer2.toString())
            .bind("created", game.created.epochSecond)
            .bind("updated", game.updated.epochSecond)
            .bind("deadline", game.deadline?.epochSecond)
            .bind("player1", game.player1.id)
            .bind("player2", game.player2.id)
            .execute()
    }


    override fun getById(id: UUID): Game? =
        handle.createQuery(
            """
           select games.id, games.state, games.boardPlayer1,games.boardPlayer2, games.created, games.updated, games.deadline,
                users_1.id as player1_id, users_1.username as player1_username, users_1.password_validation as player1_password_validation, users_1.score as player1_score,
                users_2.id as player2_id, users_2.username as player2_username, users_2.password_validation as player2_password_validation, users_2.score as player2_score
           from Games games  
           inner join Users users_1 on games.player1 = users_1.id
           inner join Users users_2 on games.player2 = users_2.id
           where games.id = :id
        """
        )
            .bind("id", id)
            .mapTo<GameDbModel>()
            .singleOrNull()
            ?.run {
                toGame()
            }

    override fun update(game: Game): Game {
        handle.createUpdate(
            """
            update Games
            set state=:state, boardPlayer1=:boardPlayer1,boardPlayer2=:boardPlayer2, updated=:updated, deadline=:deadline
            where id=:id
        """
        )
            .bind("id", game.id)
            .bind("state", game.state)
            .bind("boardPlayer1", game.boardPlayer1.toString())
            .bind("boardPlayer2", game.boardPlayer2.toString())
            .bind("updated", game.updated.epochSecond)
            .bind("deadline", game.deadline?.epochSecond)
            .execute()
        return getById(game.id)!!
    }

    override fun getGamesByUser(username: String): MutableList<Game> {

        val query = handle.createQuery(
            """select games.id, games.state, games.boardPlayer1,games.boardPlayer2, games.created, games.updated, games.deadline,
                 users_1.id as player1_id, users_1.username as player1_username, users_1.password_validation as player1_password_validation, users_1.score as player1_score,
                 users_2.id as player2_id, users_2.username as player2_username, users_2.password_validation as player2_password_validation, users_2.score as player2_score
                         from Games games
                         inner join Users users_1 on games.player1 = users_1.id
                         inner join Users users_2 on games.player2 = users_2.id
                        where (player1 in (
                                  SELECT id from users where username = :username)
                          OR player2 in (SELECT id from users where username = :username))"""

        )
            .bind("username", username)
            .mapTo<GameDbModel>()
            .toList()

            val returnList = mutableListOf<Game>()

            for (result in query) {
                returnList.add(result.toGame())
            }
            return returnList

    }


}


class GameDbModel(
    val id: UUID,
    val state: Game.State,
    val boardPlayer1: Board,
    val boardPlayer2: Board,
    val created: Instant,
    val updated: Instant,
    val deadline: Instant?,
    @Nested("player1")
    val player1: User,
    @Nested("player2")
    val player2: User,
) {
    fun toGame() = Game(
        id, state, boardPlayer1, boardPlayer2, created, updated, deadline,
        player1, player2
    )
}
