package com.example.todoapp.core.network

import android.util.ArrayMap
import android.util.Log
import retrofit2.Response
import java.io.IOException
import java.util.Objects
import java.util.regex.Pattern

class ApiResponse<T> {
    private val code: Int
    private val body: T?
    private val errorMessage: String?
    private val links: Map<String, String>

    constructor(error: Throwable) {
        code = 500
        body = null
        errorMessage = error.message
        links = emptyMap()
    }

    constructor(response: Response<T>) {
        code = response.code()
        if (response.isSuccessful) {
            body = response.body()
            errorMessage = null
        } else {
            var message: String? = null
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody()!!.string()
                } catch (ex: IOException) {
                    Log.d(ex.message, "Error while parsing response")
                }
            }
            if (message == null || message.trim { it <= ' ' }.isEmpty()) {
                message = response.message()
            }
            errorMessage = message
            body = null
        }
        val linkHeader = response.headers()["link"]
        if (linkHeader == null) {
            links = emptyMap()
        } else {
            links = ArrayMap()
            val matcher = LINK_PATTERN.matcher(linkHeader)
            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links.put(matcher.group(2), matcher.group(1))
                }
            }
        }
    }

    val nextPage: Int?
        get() {
            val next = links[NEXT_LINK] ?: return null
            val matcher = PAGE_PATTERN.matcher(next)
            return if (!matcher.find() || matcher.groupCount() != 1) {
                null
            } else try {
                Objects.requireNonNull(matcher.group(1)).toInt()
            } catch (ex: NumberFormatException) {
                Log.d("cannot parse next", next)
                null
            }
        }
    val isSuccessful: Boolean
        get() = code in (200..299)

    companion object {
        private val LINK_PATTERN = Pattern
            .compile("<([^>]*)>\\s*;\\s*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        const val NEXT_LINK = "next"
    }
}