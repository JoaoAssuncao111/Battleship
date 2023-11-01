package dawleic51d09.model

import dawleic51d09.model.Board
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class BoardTest {
    //RUN ALL TESTS WITH BOARD SIZE 2
    @Test
    fun serializeNoShipsTest() {

        val board = Board.create()
        val s = board.toString()
        var expected  = "WATER/null"
        for(i in 1 until board.board.size * board.board.size){expected += ",WATER/null"}
        assertEquals(expected, s)
        val newBoard = Board.create()
        newBoard.fromString(s)
        board.assertBoardEquals(newBoard)
    }
    @Test
    fun serializeWithShipsTest(){

        val board = Board.create()
        board.placeOnBoard(Ship(0,"Submarine"), Position(0,0),Board.Direction.V)
        val expected = "SHIP/Submarine/0,WATER/null,SHIP/Submarine/0,WATER/null"
        val actual = board.toString()
        assertEquals(expected,actual)
    }
    @Test
    fun deserializeNoShips(){
        val actual = Board.create()
        val expected = Board.create()
        val boardString = "WATER/null,WATER/null,WATER/null,WATER/null"
        actual.fromString(boardString)
        assertEquals(actual,Board(expected.board))
    }

    @Test
    fun deserializeWithShipsTest(){
        val actual = Board.create()
        val expected = Board.create()
        expected.placeOnBoard(Ship(0,"Submarine"), Position(0,1),Board.Direction.V)
        val boardString = "WATER/null,SHIP/Submarine/0,WATER/null,SHIP/Submarine/0"
        actual.fromString(boardString)
        actual.assertBoardEquals(Board(expected.board))
    }
}