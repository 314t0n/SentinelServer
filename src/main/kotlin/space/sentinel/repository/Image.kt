package space.sentinel.repository

import java.time.OffsetDateTime

data class Image(val bytes: ByteArray, val date: OffsetDateTime)