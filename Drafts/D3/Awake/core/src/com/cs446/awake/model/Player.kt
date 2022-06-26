package com.cs446.awake.model

class Player(charName: String, HP: Int, energy: Int, strength: Int, deck: Deck, state: MutableList<State>) : Character(charName, HP, energy, strength, deck, state) {
    override fun drawCard() {
        for (i in 1..5) {
            super.drawCard()
        }
    }

}