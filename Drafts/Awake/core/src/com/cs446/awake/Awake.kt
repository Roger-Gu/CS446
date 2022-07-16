package com.cs446.awake

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Array
import com.cs446.awake.model.*
import com.cs446.awake.ui.BattleEnterScreen
import com.cs446.awake.ui.BattleScreen
import com.cs446.awake.utils.BaseScreen

// maybe better to create a BaseGame class and extend from there
class Awake : Game() {

    var board: Board

    companion object {
        const val TITLE = "AWAKE"
        lateinit var game: Awake
        fun setActiveScreen(s: BaseScreen) {
            game.setScreen(s)
        }
    }


    init {
        game = this
        // The below code are only for demo use, not for final project.
        // Create demo player
        val playerDeck = getTestDeck() // A function in file testDeck.kt
        val playerStates : MutableList<State> = mutableListOf()
        val player = Player("Hero",300, 10, 10, playerDeck, playerStates, PlayerType.Human)
        // Create demo enemy
        val enemyDeck = getTestDeck()
        val enemyImage = Array<String?>(arrayOf("skeleton1.png","skeleton2.png","skeleton3.png","skeleton2.png"))
        val enemyStates : MutableList<State> = mutableListOf()
        val enemy = Enemy(enemyImage,"Enemy",100, 99, 99, enemyDeck, enemyStates, PlayerType.AI)
        // Create demo board.
        board = Board(player, enemy)
    }

    override fun create() {
        // Test Battle View
        setActiveScreen(BattleScreen(board))
    }

    override fun dispose() {
    }
}