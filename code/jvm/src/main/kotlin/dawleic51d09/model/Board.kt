package dawleic51d09.model

import dawleic51d09.model.PlayableFleet.Companion.playableFleet

class Board(var board: Array<Array<Tile>>) {

    var initialFleet = InitialFleet.create()
    private var shipDirection = Direction.H

    var playableFleet: MutableMap<String, MutableList<Ship?>> = mutableMapOf(
        "Carrier" to mutableListOf(null),
        "Battleship" to mutableListOf(null, null),
        "Cruiser" to mutableListOf(null, null, null),
        "Submarine" to mutableListOf(null,null,null,null)
    )

    fun fromString(s: String) {
        val tiles = s.split(",")
        var idx = 0
        repeat(boardSize) { row ->
            repeat(boardSize) { col ->
                val splitTiles = tiles[idx].split("/")
                val tileContent: Content = when (splitTiles[0]) {
                    "WATER" -> Content.WATER
                    "HIT_WATER" -> Content.HIT_WATER
                    "SHIP" -> Content.SHIP
                    "HIT_SHIP" -> Content.HIT_SHIP
                    "SUNK_SHIP" -> Content.SUNK_SHIP
                    else -> {
                        TODO("lançar erro")
                    }
                }
                val tileShipName = splitTiles[1]
                val ship: Ship? = if (splitTiles.size > 2) {

                    val tileShipId = Integer.parseInt(splitTiles[2])
                    val playableFleetShip = playableFleet[tileShipName]!!.size
                    if (playableFleet[tileShipName]!![tileShipId] == null)
                        playableFleet[tileShipName]!![tileShipId] = Ship(tileShipId, tileShipName)

                    playableFleet[tileShipName]!![tileShipId]
                } else {
                    null
                }
                if (tileContent == Content.HIT_SHIP) ship!!.shipTiles--
                if (tileContent == Content.SUNK_SHIP) ship!!.shipTiles = 0
                //val ship = if(tileShipName == "null") null else Ship(tileShipName)
                mutate(Position(row, col), Tile(tileContent, ship))
                //newBoard.setTile(Position(row,col),Tile(tileContent,Ship(tileShipName)))
                idx++

            }

        }
    }

    companion object {

        const val boardSize = 10
        fun create(): Board =
            Board(
                Array(boardSize) {
                    Array(boardSize) { Tile(Content.WATER, null) }
                })
    }


    enum class Direction(val char: Char) { H('H'), V('V'); }

    //changes direction when the frontend button isclicked
    fun changeDirection() =
        if (shipDirection == Direction.H) shipDirection = Direction.V else shipDirection = Direction.H

    fun getTile(position: Position) = board[position.row][position.col]

    fun setTile(position: Position, tile: Tile): Board {
        board[position.row][position.col] = tile
        return Board(board)
    }

    fun mutate(position: Position, tile: Tile) = Board(
        board.clone().also {
            board[position.row][position.col] = tile
        }
    )

    fun isShip(position: Position): Boolean {
        return (getTile(position).ship != null)
    }


    fun placeOnBoard(ship: Ship, position: Position, direction: Direction): Boolean {
        //PLAYER HAS TO DEFINE A DIRECTION AND A POSITION
        if (!canPlace(ship, position, direction)) {
            //TODO(EXCEÇÃO)
            return false
        } else {
            val removedShip = initialFleet.removeShip(ship.name)
            if (direction == Direction.H) {
                for (i in position.col until position.col + ship.size) {
                    //mutate(Position(position.row,i),Tile(Content.SHIP, ship))
                    board[position.row][i] = Tile(Content.SHIP, removedShip)
                    //alterar talvez o add para la dentro ter o retorno do remove do initalFleet (estetica)

                }
                playableFleet[ship.name]!!.add(removedShip)

            } else {

                for (j in position.row until position.row + ship.size) {
                    //mutate(Position(j,position.col),Tile(Content.SHIP, ship))
                    board[j][position.col] = Tile(Content.SHIP, removedShip)
                }
                playableFleet[ship.name]!!.add(removedShip)
            }
        }

        return true
    }

    private fun canPlace(ship: Ship, position: Position, direction: Direction): Boolean {
        val row = position.row
        val col = position.col
        if (direction == Direction.H) {
            for (i in col until col + ship.size) {
                if (board[row][i].content != Content.WATER || col + ship.size - 1 > boardSize) return false
            }
        } else {
            for (j in row until row + ship.size) {
                if (board[j][col].content != Content.WATER || row + ship.size - 1 > boardSize) return false
            }
        }
        return true
    }

    fun canShoot(position: Position): Boolean {
        return (board[position.row][position.col].content == Content.WATER || board[position.row][position.col].content == Content.SHIP)
    }


    /* fun hasWon(): Boolean {
         for (s in playableFleet.keys) {
             playableFleet[s]!!.forEach { it.shipTiles.forEach { if (it.content != Content.SUNK_SHIP) return false } }

         }
         return true
     }*/

    fun hasWon(): Boolean {
        for (s in playableFleet.keys) {
            playableFleet[s]!!.forEach {
                //TIRAR IF NOT NULL APENAS PARA TESTE
                if (it != null) {
                    if (it.shipTiles != 0)
                        return false
                }
            }

        }
        return true
    }


    fun isShipDown(ship: Ship): Boolean {
        //Verifies if ship is down (all tiles hit) and if so changed every tile to sunk
        return ship.shipTiles == 0
        /*ship.shipTiles.forEach { if (it.content == Content.SHIP) return false }
        return true*/
    }

    /*fun sinkShip(ship: Ship) {
        ship.shipTiles.forEach { it.content = Content.SUNK_SHIP }
    }*/

    fun sinkShip(tile: Tile) {
        val shipTile = tile.ship
        board.forEach { row ->
            row.forEach {
                if (it.ship == shipTile) {
                    it.content = Content.SUNK_SHIP
                }
            }
        }

    }

    override fun toString(): String = board.flatMap { row ->
        row.map { if (it.ship == null) it.content.name + "/null" else it.content.name + "/" + it.ship.name + "/" + it.ship.id }
    }.joinToString(",")


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (!board.contentDeepEquals(other.board)) return false

        return true
    }


    override fun hashCode(): Int {
        return board.contentDeepHashCode()
    }

    fun assertBoardEquals(newBoard: Board) = board.toString() == newBoard.toString()

}
