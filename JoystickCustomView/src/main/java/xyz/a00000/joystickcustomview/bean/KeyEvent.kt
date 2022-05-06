package xyz.a00000.joystickcustomview.bean

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming( PropertyNamingStrategies.UpperCamelCaseStrategy::class)
class KeyEvent(var status: Int, var type: Int): GameEvent(Xbox360Type.KEY) {
    override fun toString(): String {
        return "KeyEvent(status=$status, type=$type)"
    }
}