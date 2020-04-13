package space.sentinel.domain

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import java.util.*

open class Notification @JsonCreator constructor(
        @param:JsonProperty("timestamp") val timestamp: OffsetDateTime,
        @param:JsonProperty("message") val message: String,
        @param:JsonProperty("image") val image: Optional<ByteArray>)

class Chill  @JsonCreator constructor(@JsonProperty("timestamp") val ts: OffsetDateTime) : Notification(ts, "Chill", Optional.empty())

@Deprecated("replaced with api kojos")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class MotionDetectAlert  @JsonCreator constructor(
        @JsonProperty("timestamp") val ts: OffsetDateTime,
        @JsonProperty("image") val frame: ByteArray) : Notification(ts, "Motion detected!", Optional.of(frame)) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MotionDetectAlert

        if (ts != other.ts) return false
        if (!frame.contentEquals(other.frame)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ts.hashCode()
        result = 31 * result + frame.contentHashCode()
        return result
    }
}