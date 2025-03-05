// TODO: Update to match your plugin's package name.
package com.teqanta.ggpgs

import android.util.Log
import android.widget.Toast
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.UsedByGodot
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import com.google.android.gms.games.Player
import com.google.android.gms.tasks.Task
import org.godotengine.godot.plugin.SignalInfo

class GodotAndroidPlugin(godot: Godot): GodotPlugin(godot) {

    override fun getPluginName() = "GGPGS"
    private lateinit var gamesSignInClient: GamesSignInClient

    override fun onGodotSetupCompleted() {
        super.onGodotSetupCompleted()
        activity?.let { PlayGamesSdk.initialize(it) }

        gamesSignInClient = activity?.let { PlayGames.getGamesSignInClient(it) }!!
        gamesSignInClient.isAuthenticated().addOnCompleteListener { isAuthenticatedTask ->
            val isAuthenticated = isAuthenticatedTask.isSuccessful &&
                    isAuthenticatedTask.result?.isAuthenticated == true

            if (isAuthenticated) {
                activity?.let {
                    PlayGames.getPlayersClient(it).currentPlayer.addOnCompleteListener { mTask: Task<Player> ->
                        if (mTask.isSuccessful) {
                            Log.i("userDetailsSignal", "${mTask.result}")
                            emitSignal("userDetailsSignal", "${mTask.result}")
                            Toast.makeText(
                                activity,
                                "Logged in ${mTask.result.playerId}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                activity,
                                "Error ${mTask.exception}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(activity, "Not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getPluginSignals(): MutableSet<SignalInfo> {
        val signals: MutableSet<SignalInfo> = mutableSetOf();
        signals.add(SignalInfo("userDetailsSignal", String::class.java))
        return signals
    }

    @UsedByGodot
    private fun login() {
        runOnUiThread {
            gamesSignInClient.signIn().addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    activity?.let {
                        PlayGames.getPlayersClient(it).currentPlayer.addOnCompleteListener { mTask: Task<Player> ->
                            if (mTask.isSuccessful) {
                                Log.i("userDetailsSignal", "${mTask.result}")
                                emitSignal("userDetailsSignal", "${mTask.result}")
                                Toast.makeText(
                                    activity,
                                    "Logged in ${mTask.result.playerId}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Error ${mTask.exception}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "Error ${signInTask.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
