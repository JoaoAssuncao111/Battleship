package dawleic51d09.repository

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}