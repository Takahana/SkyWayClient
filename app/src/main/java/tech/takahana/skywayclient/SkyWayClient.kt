package tech.takahana.skywayclient

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.skywayclient.R
import io.skyway.Peer.*
import io.skyway.Peer.Browser.MediaConstraints
import io.skyway.Peer.Browser.MediaStream
import io.skyway.Peer.Browser.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

class SkyWayClient(private val activity: Activity) {
    private val option = PeerOption().apply {
        key = activity.getString(R.string.skyway_api_key)
        domain = activity.getString(R.string.skyway_domain)
        debug = Peer.DebugLevelEnum.ALL_LOGS
    }
    private var peer: Peer? = null
    private var room: Room? = null
    private var ownPeerId: String? = null
    private var localStream: MediaStream? = null

    fun initialize(): SkyWayClient {
        requestPermission()
        return this
    }

    fun connect() = callbackFlow<SkyWayEvent.PeerEvent> {
        if (!checkPermission()) {
            close(Exception("Permission denied."))
        } else {
            peer = Peer(activity, option)
            // set Peer event callback
            peer?.run {
                on(Peer.PeerEventEnum.CLOSE) {
                    offer(SkyWayEvent.from(Peer.PeerEventEnum.CLOSE))
                }
                on(Peer.PeerEventEnum.DISCONNECTED) {
                    offer(SkyWayEvent.from(Peer.PeerEventEnum.DISCONNECTED))
                }
                on(Peer.PeerEventEnum.ERROR) {
                    val error = it as PeerError
                    offer(SkyWayEvent.from(Peer.PeerEventEnum.ERROR))
                }

                // open
                on(Peer.PeerEventEnum.OPEN) { id ->
                    ownPeerId = id.toString()
                    offer(SkyWayEvent.from(Peer.PeerEventEnum.OPEN))
                }
            }
        }

        awaitClose {
            this@SkyWayClient.close()
        }
    }.flowOn(Dispatchers.IO)

    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_CODE
        )
    }

    fun joinRoom(roomId: String, listenOnly: Boolean) = callbackFlow<SkyWayEvent.RoomEvent> {
        startLocalStream()
        if (ownPeerId == null || localStream == null) {
            close()
        } else {
            val option = RoomOption().apply {
                mode = RoomOption.RoomModeEnum.SFU
                if (!listenOnly) stream = localStream
            }
            room = peer?.joinRoom(roomId, option)

            // set Room event callback
            room?.run {
                on(Room.RoomEventEnum.OPEN) {
                    offer(SkyWayEvent.from(Room.RoomEventEnum.OPEN))
                }
                on(Room.RoomEventEnum.CLOSE) {
                    room = null
                    offer(SkyWayEvent.from(Room.RoomEventEnum.CLOSE))
                }
                on(Room.RoomEventEnum.ERROR) {
                    offer(SkyWayEvent.from(Room.RoomEventEnum.ERROR))
                }
                on(Room.RoomEventEnum.PEER_JOIN) {
                    offer(SkyWayEvent.from(Room.RoomEventEnum.PEER_JOIN))
                }
                on(Room.RoomEventEnum.PEER_LEAVE) {
                    offer(SkyWayEvent.from(Room.RoomEventEnum.PEER_LEAVE))
                }
                on(Room.RoomEventEnum.STREAM) {
                    offer(SkyWayEvent.from(Room.RoomEventEnum.STREAM))
                }
                on(Room.RoomEventEnum.REMOVE_STREAM) {
                    offer(SkyWayEvent.from(Room.RoomEventEnum.REMOVE_STREAM))
                }
            }
        }

        awaitClose {
            closeRoom()
        }
    }.flowOn(Dispatchers.IO)

    private fun startLocalStream() {
        Navigator.initialize(peer)
        val constraints = MediaConstraints().apply {
            videoFlag = false
            audioFlag = true
        }
        localStream = Navigator.getUserMedia(constraints)
    }

    fun close() {
        closeRoom()
        localStream?.close()
        Navigator.terminate()
        peer?.run {
            on(Peer.PeerEventEnum.OPEN, null)
            on(Peer.PeerEventEnum.CONNECTION, null)
            on(Peer.PeerEventEnum.CALL, null)
            on(Peer.PeerEventEnum.CLOSE, null)
            on(Peer.PeerEventEnum.DISCONNECTED, null)
            on(Peer.PeerEventEnum.ERROR, null)
            disconnect()
            destroy()
        }
    }

    private fun closeRoom() {
        room?.run {
            on(Room.RoomEventEnum.OPEN, null)
            on(Room.RoomEventEnum.CLOSE, null)
            on(Room.RoomEventEnum.ERROR, null)
            on(Room.RoomEventEnum.PEER_JOIN, null)
            on(Room.RoomEventEnum.PEER_LEAVE, null)
            on(Room.RoomEventEnum.STREAM, null)
            on(Room.RoomEventEnum.REMOVE_STREAM, null)
            close()
        }
    }

    companion object {
        private const val REQUEST_CODE = 2000
    }
}
