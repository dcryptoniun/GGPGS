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
import com.teqanta.ggpgs.signal.SignInSignals.userAuthenticated
import com.teqanta.ggpgs.signal.getSignals
import org.godotengine.godot.plugin.SignalInfo
import com.google.gson.Gson
import com.teqanta.ggpgs.player.PlayerData
import com.teqanta.ggpgs.utils.toStringAndSave

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
            emitSignal(
                godot,
                BuildConfig.GODOT_PLUGIN_NAME,
                userAuthenticated,
                isAuthenticatedTask.result.isAuthenticated
            )
            if (isAuthenticated) {
                activity?.let {
                    // In both places where we handle player data (onGodotSetupCompleted and login)
                    PlayGames.getPlayersClient(it).currentPlayer.addOnCompleteListener { mTask: Task<Player> ->
                        if (mTask.isSuccessful) {
                            val player = mTask.result
                            val playerData = PlayerData(
                                playerId = player.playerId,
                                displayName = player.displayName,
                                iconImageUri = player.iconImageUri?.toStringAndSave( godot,
                                    "iconImageUri",
                                    player.playerId)
                            )
                            val playerJson = Gson().toJson(playerData)
                            Log.i("userDetailsSignal", playerJson)
                            emitSignal("userDetailsSignal", playerJson)
                            emitSignal(
                                godot,
                                BuildConfig.GODOT_PLUGIN_NAME,
                                userAuthenticated,
                                isAuthenticatedTask.result.isAuthenticated
                            )

                        } else {
                            emitSignal(
                                godot,
                                BuildConfig.GODOT_PLUGIN_NAME,
                                userAuthenticated,
                                false
                            )
                        }
                    }
                }
            } else {
                Toast.makeText(activity, "Not logged in", Toast.LENGTH_SHORT).show()
                emitSignal(
                    godot,
                    BuildConfig.GODOT_PLUGIN_NAME,
                    userAuthenticated,
                    false
                )
            }
        }
    }

    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return getSignals()
    }

    @UsedByGodot
    private fun login() {
        runOnUiThread {
            gamesSignInClient.signIn().addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    activity?.let {
                        PlayGames.getPlayersClient(it).currentPlayer.addOnCompleteListener { mTask: Task<Player> ->
                            if (mTask.isSuccessful) {
                                val playerJson = Gson().toJson(mTask.result)
                                Log.i("userDetailsSignal", "${mTask.result}")
                                emitSignal("userDetailsSignal", playerJson)

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
