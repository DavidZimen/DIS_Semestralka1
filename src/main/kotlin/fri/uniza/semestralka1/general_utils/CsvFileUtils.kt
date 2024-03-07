package fri.uniza.semestralka1.general_utils

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException

/**
 * Simple writer of [Double] values to CSV file.
 */
fun writeToCsv(fileName: String, data: List<Double>) {
    if (data.isEmpty()) return

    try {
        BufferedWriter(FileWriter(fileName)).use { writer ->
            for (value in data) {
                writer.write(value.toString().replace(".", ","))
                writer.newLine()
            }
            println("Data has been written to $fileName")
        }
    } catch (e: IOException) {
        println("Error writing to file: ${e.message}")
    }
}