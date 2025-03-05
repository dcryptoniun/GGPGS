package com.teqanta.ggpgs

import android.util.Log
import android.widget.Toast
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import com.google.android.gms.games.Player
import com.google.android.gms.tasks.Task
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot


class ggps_plugin(godot: Godot) : GodotPlugin(godot) {
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
                fetchCurrentPlayerInfo()  // Fetch player info on successful authentication
            }
        }
    }
    private fun fetchCurrentPlayerInfo() {
        activity?.let {
            PlayGames.getPlayersClient(it).currentPlayer.addOnCompleteListener { mTask: Task<Player> ->
                if (mTask.isSuccessful) {
                    Log.i("userDetailsSignal", "${mTask.result}")
                    emitSignal("userDetailsSignal", mTask.result.playerId, mTask.result.displayName)
                    Toast.makeText(activity, "Logged in ${mTask.result.playerId}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Error ${mTask.exception}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    @UsedByGodot
    fun showLeaderboard(leaderboardId: String) {
        activity?.let {
            PlayGames.getLeaderboardsClient(it).getLeaderboardIntent(leaderboardId).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activity!!.startActivity(task.result)
                } else {
                    Log.e("LeaderboardError", "Failed to show leaderboard: ${task.exception}")
                }
            }
        }
    }

    @UsedByGodot
    fun unlockAchievement(achievementId: String) {
        activity?.let {
            PlayGames.getAchievementsClient(it).unlock(achievementId)
        }
    }
}