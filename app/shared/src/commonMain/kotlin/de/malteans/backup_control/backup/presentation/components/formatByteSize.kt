package de.malteans.backup_control.backup.presentation.components

import kotlin.math.pow
import kotlin.math.roundToLong

fun Long.pow(n: Int) = this.toDouble().pow(n).roundToLong()
fun Long.round(size: Int) = ((this / 10L.pow(size - 2)) /  100.0).toString().removeSuffix(".0")

fun Long.formatByteSize(): String {

    return when {
        this < 10L.pow(3) -> "$this Byte"
        this < 10L.pow(6) -> "${this.round(3)} KB"
        this < 10L.pow(9) -> "${this.round(6)} MB"
        this < 10L.pow(12) -> "${this.round(9)} GB"
        else -> "${this.round(12)} TB"
    }
}