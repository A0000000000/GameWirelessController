package xyz.a00000.gamewirelesscontroller.bean

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy::class)
class JoystickEvent {

    var LargeMotor = 0
    var SmallMotor = 0
    var LedNumber = 0
    override fun toString(): String {
        return "JoystickEvent(LargeMotor=$LargeMotor, SmallMotor=$SmallMotor, LedNumber=$LedNumber)"
    }


}