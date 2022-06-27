package com.cs446.awake.model

class Player(charName: String, HP: Int, energy: Int, strength: Int, deck: Deck, state: MutableList<State>, playerType: PlayerType) : Character(charName, HP, energy, strength, deck, state, playerType) {

}