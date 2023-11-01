package dawleic51d09.repository.jdbi.mappers

import dawleic51d09.repository.PasswordValidationInfo
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException


class PasswordValidationInfoMapper : ColumnMapper<PasswordValidationInfo> {
    @Throws(SQLException::class)
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): PasswordValidationInfo {
        return PasswordValidationInfo(r.getString(columnNumber))
    }
}
