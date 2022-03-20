package xyz.a00000.joystickcustomview.bean

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming( PropertyNamingStrategies.UpperCamelCaseStrategy::class)
class GameEvent {

    var eventType = 0
        private set
    var type = 0
        private set
    var x = 0
        private set
    var y = 0
        private set
    var value = 0
        private set
    var status = 0
        private set

    companion object {

        fun createKeyEvent(type: Int, status: Int): GameEvent {
            val ge = GameEvent()
            ge.eventType = Xbox360Type.KEY
            ge.type = type
            ge.status = status
            return ge
        }

        fun createRockerEvent(type: Int, x: Int, y: Int): GameEvent {
            val ge = GameEvent()
            ge.eventType = Xbox360Type.AXIS
            ge.type = type
            ge.x = x
            ge.y = y
            return ge
        }

        fun createTriggerEvent(type: Int, value: Int): GameEvent {
            val ge = GameEvent()
            ge.eventType = Xbox360Type.TRIGGER
            ge.type = type
            ge.value = value
            return ge
        }

    }

}