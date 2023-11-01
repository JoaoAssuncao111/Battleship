package dawleic51d09.model

class InitialFleet(private var initialFleet: MutableMap<String, MutableList<Ship>>) {

    companion object {
        fun create(): InitialFleet = InitialFleet(
            mutableMapOf(
                "Carrier" to mutableListOf(Ship(0, "Carrier")),
                "Battleship" to mutableListOf(Ship(0, "Battleship"), Ship(1, "Battleship")),
                "Cruiser" to mutableListOf(Ship(0, "Cruiser"), Ship(1, "Cruiser"), Ship(2, "Cruiser")),
                "Submarine" to mutableListOf(
                    Ship(0, "Submarine"),
                    Ship(1, "Submarine"),
                    Ship(2, "Submarine"),
                    Ship(3, "Submarine")
                )
            )
        )
    }

    fun removeShip(shipName: String): Ship {
        return initialFleet[shipName]!!.removeFirst()
    }

    fun isEmpty() = initialFleet.isEmpty()

}


