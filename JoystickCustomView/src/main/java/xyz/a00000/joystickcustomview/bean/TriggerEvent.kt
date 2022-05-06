package xyz.a00000.joystickcustomview.bean

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming( PropertyNamingStrategies.UpperCamelCaseStrategy::class)
class TriggerEvent(var value: Int, var type: Int): GameEvent(Xbox360Type.TRIGGER) {
    override fun toString(): String {
        return "TriggerEvent(value=$value, type=$type)"
    }
}