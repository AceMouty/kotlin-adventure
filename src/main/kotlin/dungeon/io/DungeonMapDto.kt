package dungeon.io

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DungeonMapDto(
    val startRoom: Int,
    val rooms: List<RoomDto>,
    val connections: List<ConnectionDto>
)

@Serializable
data class RoomDto(
    val id: Int,
    val name: String,
    @SerialName("coords") val coordinates: RoomCoordinatesDto
)

@Serializable
data class RoomCoordinatesDto(val x: Int, val y: Int)

@Serializable
data class ConnectionDto(
    val from: Int,
    val direction: String,
    val to: Int,
    val locked: Boolean = false,
    val hidden: Boolean = false,
    val requires: String? = null
)