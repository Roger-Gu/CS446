package com.cs446.awake.model

import com.badlogic.gdx.utils.Array

class Deck {
    var deck: Array<Card> = Array<Card>()

    fun addCard(card: Card) {
        deck.add(card)
    }

    fun isEmpty() : Boolean {
        return deck.isEmpty
    }

    fun pop() : Card {
        return deck.pop()
    }

    fun shuffle() {
        deck.shuffle()
    }

    fun count() : Int {
        return deck.size
    }

}