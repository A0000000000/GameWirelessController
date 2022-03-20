package xyz.a00000.gamewirelesscontroller.bean

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import xyz.a00000.joystickcustomview.bean.GameEvent

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
open class TransferObject constructor(event: GameEvent?, type: Int?, message: String?) {

    companion object {

        @JvmStatic
        val TYPE_GAME_EVENT = 0
        @JvmStatic
        val TYPE_JOYSTICK_EVENT = 1;

    }

    constructor() : this(null,null,null){}

    var gameEvent: GameEvent? = event
    var joystickEvent: JoystickEvent? = null
    var type:Int? = type
    var message: String? = message

}