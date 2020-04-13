package space.sentinel.repository

import org.slf4j.LoggerFactory

class DropboxRepository(private val inMemoryRepository: InMemoryRepository) {

    val logger = LoggerFactory.getLogger("SentinelServer")

    fun save(img: Image){
        logger.info("Img saved. Size: %d, Date: %s",img.bytes.size, img.date.toString())
        inMemoryRepository.save(img) // todo make it reactive
    }

}