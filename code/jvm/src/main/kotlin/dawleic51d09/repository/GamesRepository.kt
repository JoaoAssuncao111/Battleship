package dawleic51d09.repository;

import dawleic51d09.model.Game;
import dawleic51d09.model.User
import dawleic51d09.repository.jdbi.GameDbModel

import java.util.UUID;

interface GamesRepository {

    fun insert(game: Game)
    fun getById(id:UUID): Game?
    fun update(game: Game): Game

    fun getGamesByUser(username: String): MutableList<Game>

}