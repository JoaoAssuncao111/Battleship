package dawleic51d09.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import dawleic51d09.repository.jdbi.mappers.BoardMapper
import dawleic51d09.repository.jdbi.mappers.InstantMapper
import dawleic51d09.repository.jdbi.mappers.PasswordValidationInfoMapper
import dawleic51d09.repository.jdbi.mappers.TokenValidationInfoMapper
import org.jdbi.v3.postgres.PostgresPlugin


fun Jdbi.configure(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(PasswordValidationInfoMapper())
    registerColumnMapper(TokenValidationInfoMapper())
    registerColumnMapper(BoardMapper())
    registerColumnMapper(InstantMapper())

    return this
}