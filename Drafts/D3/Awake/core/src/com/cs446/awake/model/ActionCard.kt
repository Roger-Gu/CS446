package com.cs446.awake.model

class ActionCard(cardName: String, img: String, usage: String, val energyCost: Int, val strengthCost: Int, val damage: Int, val effect: List<State>) : Card(cardName, img, usage)
{
    override fun use(){}
}