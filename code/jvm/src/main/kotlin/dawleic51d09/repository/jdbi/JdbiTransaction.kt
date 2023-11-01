package dawleic51d09.repository.jdbi



import org.jdbi.v3.core.Handle
import dawleic51d09.repository.GamesRepository
import dawleic51d09.repository.Transaction
import dawleic51d09.repository.UsersRepository

class JdbiTransaction(
    private val handle: Handle
) : Transaction {

    override val usersRepository: UsersRepository by lazy { JdbiUsersRepository(handle) }

    override val gamesRepository: GamesRepository by lazy { JdbiGamesRepository(handle) }

    override fun rollback() {
        handle.rollback()
    }
}
