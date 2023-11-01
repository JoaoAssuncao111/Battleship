package dawleic51d09.model

data class Tile(var content: Content, val ship: Ship?)

enum class Content(s: String){
    WATER("Water"),
    HIT_WATER("Hit water"),
    HIT_SHIP("Hit ship",),
    SUNK_SHIP("Sunk ship"),
    SHIP("ship")
}
