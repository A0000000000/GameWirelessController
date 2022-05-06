package xyz.a00000.joystickcustomview.bean

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming( PropertyNamingStrategies.UpperCamelCaseStrategy::class)
class RockerEvent(var x: Int, var y: Int, var type: Int): GameEvent(Xbox360Type.AXIS) {
    override fun toString(): String {
        return "RockerEvent(x=$x, y=$y, type=$type)"
    }
}