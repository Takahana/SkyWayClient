package com.example.skywayclient

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import tech.takahana.skywayclient.SkyWayClient
import tech.takahana.skywayclient.SkyWayEvent

class MainActivity : AppCompatActivity() {

    private lateinit var skyWayClient: SkyWayClient
    private val ROOM_ID = "room1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!::skyWayClient.isInitialized) skyWayClient =
            SkyWayClient(this).initialize()

        start_btn.setOnClickListener {
            startConnection()
        }
    }

    private fun startConnection() {
        lifecycleScope.launchWhenResumed {
            skyWayClient.connect()
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
