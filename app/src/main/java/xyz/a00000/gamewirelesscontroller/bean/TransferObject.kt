package xyz.a00000.gamewirelesscontroller.bean

import com.fasterxml.jackson.databind.ObjectMapper

open class TransferObject(data: Map<String, Any>, type: Int, message: String) {

    companion object {

        @JvmStatic
        val objectMapper = ObjectMapper()

        @JvmStatic
        fun fromJson(json: String): TransferObject {
            return objectMapper.readValue(json, TransferObject::class.java)
        }

    }

    var data: Map<String, Any>? = null;
    var type: Int = 0
    var message: String = ""

    init {
        this.data = data
        this.type = type
        this.message = message
    }

    fun toJson(): String {
        return objectMapper.writeValueAsString(this);
    }

}