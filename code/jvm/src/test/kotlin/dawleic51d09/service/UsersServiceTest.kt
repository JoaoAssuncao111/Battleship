package dawleic51d09.service

import dawleic51d09.model.UserLogic
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import dawleic51d09.repository.Transaction
import dawleic51d09.repository.TransactionManager
import dawleic51d09.repository.jdbi.JdbiTransaction
import dawleic51d09.repository.jdbi.configure
import dawleic51d09.utils.Sha256TokenEncoder
import java.util.*

class UsersServiceTest {

    private val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL")

    private val jdbi = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL("$jdbcDatabaseURL?user=postgres&password=BURUBESTA")
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

    @Test
    fun `can create user, token, and retrieve by token`(): Unit =
        testWithTransactionManagerAndRollback { transactionManager ->

            // given: a user service
            val userService = UsersService(
                transactionManager,
                UserLogic(),
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
            )

            // when: creating a user

            // then: the creation is successful
            when (val createUserResult = userService.createUser("bob", "changeit",0)) {
                is Either.Left -> fail("Unexpected $createUserResult")
                is Either.Right -> assertTrue(createUserResult.value.isNotEmpty())
            }

            // when: creating a token
            val createTokenResult = userService.createToken("bob", "changeit")

            // then: the creation is successful
            val token = when (createTokenResult) {
                is Either.Left -> Assertions.fail(createTokenResult.toString())
                is Either.Right -> createTokenResult.value
                else -> {createTokenResult.toString()}
            }

            // and: the token bytes have the expected length
            val tokenBytes = Base64.getUrlDecoder().decode(token)
            assertEquals(256 / 8, tokenBytes.size)

            // when: retrieving the user by token
            val user = userService.getUserByToken(token)

            // then: a user is found
            assertNotNull(user)

            // and: has the expected name

            assertEquals("bob", user!!.username)

        }
}