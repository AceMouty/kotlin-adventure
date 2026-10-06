package com.acemouty.adventure.dungeon

class Dungeon(
    val startRoom: Int,
    rooms: Collection<Room>,
) {
    val rooms: Collection<Room> get() = roomsById.values
    private val roomsById: Map<Int, Room> = rooms
        .map { room -> room.copy(exits = room.exits.toMap())}
        .associateBy { room -> room.id }

    /** The room with this id, or null if there is no such room. */
    fun findRoom(id: Int): Room? =
        roomsById[id]

    /**
     * All exits leaving this room, keyed by direction.
     * Null if there is no such room; empty if the room has no exits.
     */
    fun exitsFrom(id: Int): Map<Direction, Exit>? =
        findRoom(id)?.exits

    /**
     * The exit leaving this room in this direction.
     * Null if there is no such room, or the room has no exit that way.
     */
    fun exit(from: Int, direction: Direction): Exit? =
        exitsFrom(from)?.get(direction)

    /**
     * Ids of the rooms this room's exits lead to, including locked and hidden exits.
     * Null if there is no such room; empty if the room has no exits.
     */
    fun neighbors(id: Int): Set<Int>? =
        findRoom(id)?.let { room ->
            room.exits.values.map { it.to }.toSet()
        }

}

data class Room(
    val id: Int,
    val name: String,
    val coordinates: RoomCoordinates,
    val exits: Map<Direction, Exit>
)

data class Exit(
    val to: Int,
    val locked: Boolean,
    val hidden: Boolean,
    val requires: String?
)

data class RoomCoordinates(val x: Int, val y: Int)

enum class Direction(val label: String) {
    NORTH("north"),
    SOUTH("south"),
    EAST("east"),
    WEST("west");

    fun opposite(): Direction =
        when(this) {
            NORTH -> SOUTH
            SOUTH -> NORTH
            EAST -> WEST
            WEST -> EAST
        }

    companion object {
        fun fromLabelOrNull(label: String): Direction? =
            entries.firstOrNull {
                it.label == label
            }
    }
}
