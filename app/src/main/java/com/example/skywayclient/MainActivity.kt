package com.example.skywayclient

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.skyway.Peer.Peer
import io.skyway.Peer.PeerOption
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import tech.takahana.skywayclient.SkyWayClient
import tech.takahana.skywayclient.SkyWayEvent

class MainActivity : AppCompatActivity() {

    private val skyWayClient = SkyWayClient()
    private val PEER_ID = "peer1234"
    private val ROOM_ID = "room1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start_btn.setOnClickListener {
            startConnection()
        }
    }

    override fun onResume() {
        super.onResume()
        skyWayClient.requestPermission(this)
    }

    private fun startConnection() {
        val option = PeerOption().apply {
            key = getString(R.string.skyway_api_key)
            domain = getString(R.string.skyway_domain)
            debug = Peer.DebugLevelEnum.ALL_LOGS
        }
        lifecycleScope.launchWhenResumed {
            skyWayClient.connect(this@MainActivity, PEER_ID, option)
                .catch { error ->
                    // handle exception: ex) permission denied
                    Log.e("_ERROR_", "${error.message}")
                }
                .collect {
                    when (it) {
                        SkyWayEvent.PeerEvent.OPEN -> joinRoom(listenOnly = true)
                    }
                }
        }
    }

    private fun joinRoom(listenOnly: Boolean) {
        lifecycleScope.launchWhenResumed {
            skyWayClient.joinRoom(ROOM_ID, listenOnly).collect { event ->
                if (event == SkyWayEvent.RoomEvent.OPEN) {
                    // connection complete
                }
            }
        }
    }

    companion object {

        private const val MY_PERMISSIONS_REQUEST = 1000
    }
}
