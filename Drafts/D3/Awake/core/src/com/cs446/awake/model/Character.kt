package com.cs446.awake.model

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Array

abstract class Character (val charName: String, val maxHP: Int, val maxEnergy: Int, val maxStrength: Int, val deck: Deck, var state: MutableList<State>) {
    var hand: MutableList<Card> = mutableListOf()
    var energy = maxEnergy
    var strength = maxStrength
    var HP = maxHP

    fun useCard(card: Card, target: Character):Boolean {
        if (card.cost(this)) {
            card.useCard(target)
            hand.remove(card)
            return true
        }
        return false
    }

    // add try catch block for 1. empty deck 2.hand full
    open fun drawCard(){
        val c = deck.pop() // deck should shuffle when it is empty
        hand.add(c)
    }

    fun updateState(newState: State){
        state.add(newState)
    }

    fun removeState(removedStates: MutableList<String>){

    }

    fun removeState(removedState: State){
        state.remove((removedState))
    }

    open fun endRound(){
        for (s in state) {
            s.apply()
        }
        hand.clear()
        energy = maxEnergy
        strength = maxStrength
    }

    fun updateHealth(HpChange: Int){
        HP += HpChange
    }

    fun updateStrength(strengthChange: Int) {
        strength += strengthChange
    }

    fun updateEnergy(energyChange: Int) {
        energy += energyChange
    }

    fun isDead(): Boolean {
        return HP <= 0
    }

}