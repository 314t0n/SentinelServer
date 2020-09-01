package space.sentinel.service

import java.util.*

class PaginationService {

    companion object {
        const val DEFAULT_PAGE_SIZE = 5L
        const val FIRST_PAGE = 0L
    }

    fun pagination(params: Map<String, String>): Pair<Long, Long> {
        val requestedPageNumber = Optional.ofNullable(params["page"])
                .map(String::toLong)
                .map { number ->
                    number.minus(1)
                }
                .orElse(FIRST_PAGE)

        val limit: Long = requestedPageNumber * DEFAULT_PAGE_SIZE
        val offset: Long = DEFAULT_PAGE_SIZE
        return Pair(limit, offset)
    }
}