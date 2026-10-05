package dungeon.io

import com.acemouty.adventure.dungeon.Direction
import com.acemouty.adventure.dungeon.Dungeon
import com.acemouty.adventure.dungeon.Exit
import com.acemouty.adventure.dungeon.Room
import com.acemouty.adventure.dungeon.RoomCoordinates

/** Converts an already-validated file model into the domain model. */
internal fun DungeonMapDto.toDungeon(): Dungeon {
    val exitsByRoom = connections
        .groupBy { it.from }
        .mapValues { entry ->
            entry.value.associate { connection ->
                val direction = Direction.fromLabelOrNull(connection.direction)
                    ?: error("Unknown direction '${connection.direction}' leaving room ${connection.from}")
                direction to connection.toExit()
            }
        }
    val dungeonRooms = rooms.map{ roomDto ->
        roomDto.toRoom(
            exitsByRoom[roomDto.id] ?: emptyMap()
        )
    }

    return Dungeon(startRoom = startRoom, rooms = dungeonRooms)
}

private fun RoomCoordinatesDto.toRoomCoordinates(): RoomCoordinates =
    RoomCoordinates(
        x = this.x,
        y = this.y
    )

private fun ConnectionDto.toExit(): Exit =
    Exit(
        to = this.to,
        locked = this.locked,
        hidden = this.hidden,
        requires = this.requires
    )

private fun RoomDto.toRoom(exits: Map<Direction, Exit>): Room =
    Room(
        id = this.id,
        name = this.name,
        coordinates = coordinates.toRoomCoordinates(),
        exits = exits
    )