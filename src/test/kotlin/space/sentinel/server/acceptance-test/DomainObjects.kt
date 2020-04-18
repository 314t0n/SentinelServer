package space.sentinel.server.`acceptance-test`

import space.sentinel.api.NotificationRequest
import space.sentinel.api.NotificationResponse
import space.sentinel.api.NotificationType
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.OffsetDateTime
import java.util.*
import javax.imageio.ImageIO

/**
 * TestMother Object
 */
class DomainObjects {
    companion object {

        fun readImage(name: String = "test"): ByteArray {
            val testPng = File("src/test/resources/$name.png")
            val image = ImageIO.read(testPng)
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(image, "png", outputStream)
            return Base64.getEncoder().encode(outputStream.toByteArray())
        }

        val ATimeStamp = OffsetDateTime.now()
        val AlertNotificationWithImage = NotificationRequest(
                ATimeStamp,
                "id1",
                "rpi_room",
                "motion detected",
                NotificationType.ALERT,
                Optional.of(readImage()))

        val AlertNotificationWithoutImage = NotificationRequest(
                ATimeStamp,
                "id1",
                "rpi_room",
                "device disconnected",
                NotificationType.ALERT,
                Optional.empty())

        val InfoNotification = NotificationRequest(
                ATimeStamp,
                "id2",
                "rpi_door",
                "initialized",
                NotificationType.INFO,
                Optional.empty())


        val ANotificationResponse = NotificationResponse(ATimeStamp, "dbid1")
    }
}