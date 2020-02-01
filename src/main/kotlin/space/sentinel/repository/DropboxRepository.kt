package space.sentinel.repository

import org.slf4j.LoggerFactory

class DropboxRepository {

    val logger = LoggerFactory.getLogger("SentinelServer")

    fun save(img: Image){
        logger.info("Img saved. Size: %d, Date: %s",img.bytes.size, img.date.toString())
    }

}