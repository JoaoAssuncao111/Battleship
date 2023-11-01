package dawleic51d09.repository

interface Transaction {

    val usersRepository: UsersRepository

    val gamesRepository: GamesRepository

    fun rollback()
}