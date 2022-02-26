package xyz.a00000.gamewirelesscontroller.bean

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming( PropertyNamingStrategies.UpperCamelCaseStrategy::class)
open class TransferObject  constructor(data: Map<String, Any>?, type: Int, message: String) {

    companion object {

        @JvmStatic
        val objectMapper = ObjectMapper()

        @JvmStatic
        fun fromJson(json: String): TransferObject {
            return objectMapper.readValue(json, TransferObject::class.java)
        }

    }

    constructor(): this(null, 0, "") {

    }

    var data: Map<String, Any>? = null
    var type: Int = 0
    var message: String = ""

    init {
        this.data = data
        this.type = type
        this.message = message
    }

    fun toJson(): String {
        return objectMapper.writeValueAsString(this)
    }

}