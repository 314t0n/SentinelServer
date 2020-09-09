package space.sentinel.server.`acceptance-test`

import space.sentinel.api.request.NotificationRequest
import space.sentinel.api.NotificationType
import space.sentinel.api.request.LoginRequest
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
                "motion detected",
                NotificationType.ALERT,
                Optional.of(readImage()))

        val AlertNotificationWithoutImage = NotificationRequest(
                ATimeStamp,
                "id1",
                "device disconnected",
                NotificationType.ALERT,
                Optional.empty())

        val InfoNotification = NotificationRequest(
                ATimeStamp,
                "id2",
                "initialized",
                NotificationType.INFO,
                Optional.empty())


        val DeviceRequest = space.sentinel.api.request.DeviceRequest(
                apiKey = "device123",
                name = "raspberry123"
        )

        val ValidLoginRequest = LoginRequest(
                email = "test@body.ru",
                pass = LoginRequest.encodePassword("canihavehamburger?")
        )
    }
}