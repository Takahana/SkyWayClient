package tech.takahana.skywayclient

import io.skyway.Peer.Peer
import io.skyway.Peer.Room


sealed class SkyWayEvent {

    sealed class PeerEvent : SkyWayEvent() {

        object OPEN : PeerEvent()
        object CONNECTION : PeerEvent()
        object CALL : PeerEvent()
        object CLOSE : PeerEvent()
        object DISCONNECT : PeerEvent()
        object ERROR : PeerEvent()
        object UNKNOWN : PeerEvent()
    }

    sealed class RoomEvent : SkyWayEvent() {
        object STREAM : RoomEvent()
        object REMOVE_STREAM : RoomEvent()
        object OPEN : RoomEvent()
        object CLOSE : RoomEvent()
        object PEER_JOIN : RoomEvent()
        object PEER_LEAVE : RoomEvent()
        object ERROR : RoomEvent()
        object UNKNOWN : RoomEvent()
    }


    companion object {
        fun from(event: Peer.PeerEventEnum): PeerEvent {
            return when (event) {
                Peer.PeerEventEnum.OPEN -> PeerEvent.OPEN
                Peer.PeerEventEnum.CONNECTION -> PeerEvent.CONNECTION
                Peer.PeerEventEnum.CALL -> PeerEvent.CALL
                Peer.PeerEventEnum.CLOSE -> PeerEvent.CLOSE
                Peer.PeerEventEnum.DISCONNECTED -> PeerEvent.DISCONNECT
                Peer.PeerEventEnum.ERROR -> PeerEvent.ERROR
                else -> PeerEvent.UNKNOWN
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
                else -> RoomEvent.UNKNOWN
            }
        }
    }
}
