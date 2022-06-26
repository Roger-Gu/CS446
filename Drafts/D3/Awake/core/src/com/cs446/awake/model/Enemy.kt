package com.cs446.awake.model

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.cs446.awake.utils.AbstractActor
import com.cs446.awake.utils.BaseActor

class Enemy(val images: Array<String?>, charName: String, HP: Int, energy: Int, strength: Int, deck: Deck, state: MutableList<State>) : Character(charName, HP, energy, strength, deck, state) {

    // Enemy may have pre-defined hand cards and randomly use
    fun useCard(target: Character) {}

    override fun endRound() {
        super.endRound()
        // Enemy may trigger different skills
    }

}