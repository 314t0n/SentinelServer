package space.sentinel.translator

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Inject
import io.r2dbc.spi.Row
import space.sentinel.api.Device
import space.sentinel.api.Devices
import space.sentinel.api.request.DeviceRequest

class DeviceTranslator @Inject constructor(private val mapper: ObjectMapper,
                                           private val dateTimeTranslator: DateTimeTranslator) : Translator(mapper) {

    fun translate(row: Row): Device =
            Device(
                    id = row.get("id", String::class.java),
                    created = dateTimeTranslator.toDateTime(row),
                    apiKey = row.get("api_key", String::class.java),
                    name = row.get("name", String::class.java)
            )

    fun toJson(devices: Devices): String = mapper.writeValueAsString(devices)
    fun toJson(device: Device): String = mapper.writeValueAsString(device)

    fun translate(json: String): DeviceRequest = mapper.readValue(json, DeviceRequest::class.java)

}