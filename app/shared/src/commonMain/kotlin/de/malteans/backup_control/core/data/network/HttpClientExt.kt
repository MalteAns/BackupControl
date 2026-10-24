package de.malteans.backup_control.core.data.network

import de.malteans.backup_control.core.data.AnsLog
import de.malteans.backup_control.core.data.ErrorDto
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Result<T> {
    val response = try {
        execute()
    } catch (e: Exception) {
        AnsLog.e("safeCall", "Exception occurred during request execution: ${e.message}", e)
        return Result.failure(e)
    }
    AnsLog.d("safeCall", "Finished executing request. Processing response...")
    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse
): Result<T> {
    return when(response.status.value) {
        in 200..299 -> {
            try {
                Result.success(response.body<T>())
            } catch(e: Exception) {
                Result.failure(e)
            }
        }
        else -> {
            val data = try {
                val errorDto = response.body<ErrorDto>()
                "${errorDto.code}: ${errorDto.message}"
            } catch(_: Exception) {
                response.bodyAsText().ifBlank { null }
            }
            Result.failure(HttpStatusException(response.status, data))
        }
    }
}

data class HttpStatusException(val statusCode: HttpStatusCode, val data: String? = null) : Exception()
