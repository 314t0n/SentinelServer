package space.sentinel.repository

import reactor.core.publisher.Mono

/**
 * Writes image to the disk
 */
class FileImageRepository {

    /**
     * @param img Image to save
     *  @return filename
     */
    fun save(img: ImageEntity): Mono<String> {
        return Mono.just("image.jpg")
    }

}