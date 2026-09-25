package de.malteans.backup_control.services.util

import java.io.RandomAccessFile


fun readLastNLines(filePath: String?, n: Int): MutableList<String?> {
    val lines: MutableList<String?> = mutableListOf()
    if (n <= 0) return lines // Return empty list for non-positive N


    RandomAccessFile(filePath, "r").use { file ->
        val fileLength = file.length()
        if (fileLength == 0L) return lines // Empty file


        var pointer = fileLength - 1 // Start at the last byte
        var lineCount = 0
        val currentLine = StringBuilder()

        while (pointer >= 0 && lineCount < n) {
            file.seek(pointer)
            val b = file.readByte()

            if (b == '\n'.code.toByte()) { // Found a line break
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.reverse().toString()) // Reverse to correct order
                    currentLine.setLength(0) // Reset for next line
                    lineCount++
                }
            } else if (b != '\r'.code.toByte()) { // Ignore carriage returns (for CRLF)
                currentLine.append(Char(b.toUShort()))
            }
            pointer--
        }


        // Add the last line (if file doesn't end with a newline)
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.reverse().toString())
            lineCount++
        }


        // Reverse to restore natural order (last line first → first of last N lines)
        lines.reverse()


        // Handle cases where there are fewer lines than N
        return if (lines.size > n) lines.subList(lines.size - n, lines.size) else lines
    }
}