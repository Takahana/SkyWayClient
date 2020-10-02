package tech.takahana.skywayclient

import io.skyway.Peer.Peer
import io.skyway.Peer.Room


class SkyWayEvent {
    enum class PeerEvent {
        OPEN, CONNECTION, CALL, CLOSE, DISCONNECT, ERROR, NONE
    }
    enum class RoomEvent {
        STREAM, REMOVE_STREAM, OPEN, CLOSE, PEER_JOIN, PEER_LEAVE, ERROR, NONE
    }


    companion object {
        fun from(event: Peer.PeerEventEnum): PeerEvent {
            return when(event) {
                Peer.PeerEventEnum.OPEN -> PeerEvent.OPEN
                Peer.PeerEventEnum.CONNECTION -> PeerEvent.CONNECTION
                Peer.PeerEventEnum.CALL -> PeerEvent.CALL
                Peer.PeerEventEnum.CLOSE -> PeerEvent.CLOSE
                Peer.PeerEventEnum.DISCONNECTED -> PeerEvent.DISCONNECT
                Peer.PeerEventEnum.ERROR -> PeerEvent.ERROR
                else -> PeerEvent.NONE
            }
        }
        fun from(event: Room.RoomEventEnum): RoomEvent {
            return when(event) {
                Room.RoomEventEnum.STREAM -> RoomEvent.STREAM
                Room.RoomEventEnum.REMOVE_STREAM -> RoomEvent.REMOVE_STREAM
                Room.RoomEventEnum.OPEN -> RoomEvent.OPEN
                Room.RoomEventEnum.CLOSE -> RoomEvent.CLOSE
                Room.RoomEventEnum.PEER_JOIN -> RoomEvent.PEER_JOIN
                Room.RoomEventEnum.PEER_LEAVE -> RoomEvent.PEER_LEAVE
                Room.RoomEventEnum.ERROR -> RoomEvent.ERROR
                else -> RoomEvent.NONE
            }
        }
    }
}
