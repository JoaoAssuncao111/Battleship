package dawleic51d09.repository.jdbi.mappers

import dawleic51d09.model.Board
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException

class BoardMapper : ColumnMapper<Board> {
    @Throws(SQLException::class)
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): Board {
        val board = Board.create()
            board.fromString(r.getString(columnNumber))

        return board
    }
}