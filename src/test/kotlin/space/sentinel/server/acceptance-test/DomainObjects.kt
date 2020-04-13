package space.sentinel.server.`acceptance-test`

import space.sentinel.domain.MotionDetectAlert
import space.sentinel.domain.Notification
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.OffsetDateTime
import java.util.*
import javax.imageio.ImageIO

/**
 * TestMother Object
 */
class DomainObjects {
    companion object{

        fun readImage(name: String = "test"): ByteArray {
            val testPng = File("src/test/resources/$name.png")
            val image = ImageIO.read(testPng)
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(image, "png", outputStream)
            return Base64.getEncoder().encode(outputStream.toByteArray())
        }

        val ATimeStamp = OffsetDateTime.now()
        val AMotionDetectAlert = MotionDetectAlert(ATimeStamp, readImage())

    }
}