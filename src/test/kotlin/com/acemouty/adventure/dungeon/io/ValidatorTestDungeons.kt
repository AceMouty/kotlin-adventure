package com.acemouty.adventure.dungeon.io

import dungeon.io.ConnectionDto
import dungeon.io.DungeonMapDto
import dungeon.io.RoomCoordinatesDto
import dungeon.io.RoomDto
/** Three rooms in a line: 0 <-> 1 <-> 2, going north. */
val lineMap = DungeonMapDto(
    startRoom = 0,
    rooms = listOf(
        RoomDto(0, "Room 0", RoomCoordinatesDto(3, 5)),
        RoomDto(1, "Room 1", RoomCoordinatesDto(3, 6)),
        RoomDto(2, "Room 2", RoomCoordinatesDto(3, 7)),
    ),
    connections = listOf(
        ConnectionDto(0, "north", 1),
        ConnectionDto(1, "south", 0),
        ConnectionDto(1, "north", 2),
        ConnectionDto(2, "south", 1),
    ),
)

val singleRoomMap = DungeonMapDto(
    startRoom = 0,
    rooms = listOf(RoomDto(0, "Room 0", RoomCoordinatesDto(0, 0))),
    connections = emptyList(),
)

fun DungeonMapDto.withStartRoom(newStartRoomId: Int) =
    copy( startRoom = newStartRoomId)

fun DungeonMapDto.withRoom(room: RoomDto) =
    copy(rooms = listOf(*rooms.toTypedArray(), room))

fun DungeonMapDto.withConnection(connection: ConnectionDto) =
    copy(connections = listOf(*connections.toTypedArray(), connection))

fun DungeonMapDto.withoutConnection(from: Int, to: Int) =
    copy(connections = connections.filterNot { it.from == from && it.to == to })

fun DungeonMapDto.withRoomMovedTo(id: Int, coordinates: RoomCoordinatesDto) =
    copy(rooms = rooms.map { if (it.id == id) it.copy(coordinates = coordinates) else it })

fun DungeonMapDto.withDirectionChanged(from: Int, to: Int, direction: String) =
    copy(connections = connections.map {
        if (it.from == from && it.to == to) it.copy(direction = direction) else it
    })