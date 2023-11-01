package dawleic51d09.model

class Ship(val id: Int, val name: String) {

    var size: Int = when(name){
        "Carrier" -> 5
        "Battleship" -> 4
        "Cruiser" -> 3
        "Submarine" -> 2
        else -> {4 /*TODO(ERROR)*/}
    }
/*
    var shipTiles: MutableList<ShipTile> = mutableListOf()
    init {
        for (i in 0 until size){shipTiles.add(ShipTile("SHIP"))}
    }*/

    //Numero de células nao atingidas de um ship
    var shipTiles:Int = size

    override fun toString(): String {
        return this.name + "/" + this.id
    }

}
