package de.malteans.backup_control.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(
    val code: ErrorCode,
    val message: String,
)

enum class ErrorCode {
    UNKNOWN_ERROR,
    GET_BACKUPS_FAILED,
    UPDATE_BACKUPS_FAILED,
}