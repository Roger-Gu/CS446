package com.cs446.awake.model

import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
import com.badlogic.gdx.utils.Timer

// TODO - Character Methods:
// reset() reset everything (MAY not really needed)
// preRound() check its state and apply if needed
// drawCard() get character cards
// useCard() wait user input for card used, return a Card Class
// applyCard(cardUsed: Card) receive card and apply to this character

class Board (val player: Player,val enemy: Enemy) {
    private var turn: Character = player
    private var target: Character = enemy
    private var currentRound: Int = 0

    init {
        turn.reset()
        target.reset()
    }

    fun startGame() {
        print("game started")
        if (!finished()) {
            currentRound++
            if (currentRound % 2 == 1) preRound()
        }
        println("\n END GAME END GAME END GAME")
//            // Separate function for easy maintenance and upgrade add-ons in future.
//            currentRound++
//            preRound()
//            startRound()
//            endRound()
//            postRound()
//            switchTurn()
//            println("Round $currentRound")
    }
    open fun removeCard(card: ActionCard) {
        turn.removeCard(card)
    }

    private fun finished(): Boolean {
        if (player.isDead() || enemy.isDead()) {
            return true
        }
        return false
    }
    fun isAITurn(): Boolean {
        return turn != player
    }

    fun activeAI(){
        val card = turn.selectRamdomCard()
        notify(card)
        println("\nAI draw card ${card.cardName}")
    }

    private fun preRound() {
        turn.preRound()
        target.preRound()
    }

    fun checkTurn(target: Character): Boolean {
        return target == turn
    }
    fun notify(card: ActionCard) {
        target.update(card, from = turn)
        turn.update(card, from = turn)
    }

    private fun startRound() {
//        val card = turn.selectHandCard() ?: return

        // Option 1 - Notify one
        // target.useCard(Card, from = turn)
        // Option 2 - Notify everyone
//        target.update(card, from = turn)
//        turn.update(card, from = turn)
    }

    private fun endRound(){}

    fun postRound() {
        turn.postRound()
        target.postRound()
    }

    fun switchTurn() {
        val temp = turn
        turn = target
        target = temp
        startGame()
    }

    fun removeEnemy(target: Enemy) {}
}