package dawleic51d09.service

import dawleic51d09.repository.Transaction
import dawleic51d09.repository.TransactionManager
import dawleic51d09.repository.jdbi.JdbiTransaction
import dawleic51d09.repository.jdbi.configure
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource

class GameServiceTests {

    private val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")

    private val jdbi = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL("$jdbcDatabaseURL?user=postgres&password=BURUBESTA")//"jdbc:postgresql://localhost:5432/postgres?user=postgres&password=joaopedro123"
        }
    ).configure()

    private fun testWithTransactionManagerAndRollback(block: (TransactionManager) -> Unit) =
        jdbi.useTransaction<Exception>
        { handle ->

            val transaction = JdbiTransaction(handle)

            // a test TransactionManager that never commits
            val transactionManager = object : TransactionManager {
                override fun <R> run(block: (Transaction) -> R): R {
                    return block(transaction)
                    // n.b. no commit happens
                }
            }
            block(transactionManager)

            // finally, we rollback everything
            handle.rollback()
        }
   /* @Test
    fun placingPhaseTest(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->
            //DONE IN POSTMAN

          //creating a game service
            val gamesService = GamesService(transactionManager)



            val listPlacedShips : List<UserShipPlacement> = listOf(UserShipPlacement(Position(0,0), Ship(0,"Submarine"),Board.Direction.V))


            //gamesService.placingPhase(1,UUID.fromString("c3b57dc5-e04e-4ea8-b6dd-ec472022ef86"),listPlacedShips)
            gamesService.placingPhase(2,UUID.fromString("c3b57dc5-e04e-4ea8-b6dd-ec472022ef86"),listPlacedShips)

            val expectedBoard1= Board.create()
            expectedBoard1.placeOnBoard(Ship(0,"Submarine"),Position(0,0), Board.Direction.V)

            val expectedBoard2= Board.create()
            expectedBoard2.placeOnBoard(Ship(0,"Submarine"),Position(0,0), Board.Direction.V)

           val testBoard= when(val game= gamesService.placingPhase(1,UUID.fromString("c3b57dc5-e04e-4ea8-b6dd-ec472022ef86"),listPlacedShips)){
                is Either.Right-> game.value.boardPlayer1
                else -> {
                    null
                }
            }
            assertTrue {  expectedBoard1.assertBoardEquals(testBoard!!)}

        }*/
}