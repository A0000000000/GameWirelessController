package xyz.a00000.joystickcustomview.bean

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming( PropertyNamingStrategies.UpperCamelCaseStrategy::class)
open class GameEvent(var eventType: Int) {
    override fun toString(): String {
        return "GameEvent(eventType=$eventType)"
    }
}