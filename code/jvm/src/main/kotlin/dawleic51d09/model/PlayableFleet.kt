package dawleic51d09.model

class PlayableFleet {

    companion object {
        val playableFleet: MutableMap<String, MutableList<Ship>> = mutableMapOf(
            "Carrier" to mutableListOf(),
            "Battleship" to mutableListOf(),
            "Cruiser" to mutableListOf(),
            "Submarine" to mutableListOf()
        )
    }

    fun addFleet(shipName: String , ship:Ship){
        playableFleet[shipName]!!.add(ship)
    }

    fun keys() = playableFleet.keys

    fun getValue(key: String) = playableFleet[key]
}