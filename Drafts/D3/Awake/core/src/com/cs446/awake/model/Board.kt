package com.cs446.awake.model

class Board (val player: Player,val enemy: Enemy,val turn: Character,val currentRound: Int) {
    init {
        startRound()
    }
    fun removeEnemy(target: Enemy) {}

    private fun startRound() {
        turn.drawCard()
    }

    fun endRound(){}

}