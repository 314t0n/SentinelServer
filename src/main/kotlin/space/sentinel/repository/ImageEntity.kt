package space.sentinel.repository

import java.time.OffsetDateTime

data class ImageEntity(val id: String, val bytes: ByteArray, val date: OffsetDateTime)