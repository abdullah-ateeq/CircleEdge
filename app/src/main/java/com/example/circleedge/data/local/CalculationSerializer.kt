package com.example.circleedge.data.local

import com.example.circleedge.data.model.CircleInputType
import com.example.circleedge.data.model.MeasurementUnit
import com.example.circleedge.data.model.SavedCalculation
import java.math.BigDecimal

object CalculationSerializer {

    fun serializeList(list: List<SavedCalculation>): String {
        val sb = StringBuilder()
        sb.append("[")
        list.forEachIndexed { index, item ->
            if (index > 0) sb.append(",")
            sb.append(serializeItem(item))
        }
        sb.append("]")
        return sb.toString()
    }

    fun serializeItem(item: SavedCalculation): String {
        return buildString {
            append("{")
            append("\"id\":").append(quote(item.id)).append(",")
            append("\"projectName\":").append(quote(item.projectName)).append(",")
            append("\"inputType\":").append(quote(item.inputType.name)).append(",")
            append("\"inputValue\":").append(quote(item.inputValue.toPlainString())).append(",")
            append("\"unit\":").append(quote(item.unit.name)).append(",")
            append("\"radiusMm\":").append(quote(item.radiusMm.toPlainString())).append(",")
            append("\"diameterMm\":").append(quote(item.diameterMm.toPlainString())).append(",")
            append("\"circumferenceMm\":").append(quote(item.circumferenceMm.toPlainString())).append(",")
            append("\"areaMm2\":").append(quote(item.areaMm2.toPlainString())).append(",")
            append("\"timestampMillis\":").append(item.timestampMillis).append(",")
            append("\"isFavorite\":").append(item.isFavorite)
            append("}")
        }
    }

    fun deserializeList(json: String): List<SavedCalculation> {
        val trimmed = json.trim()
        if (trimmed.isEmpty() || trimmed == "[]") return emptyList()

        val results = mutableListOf<SavedCalculation>()
        val itemStrings = splitJsonArray(trimmed)

        for (itemStr in itemStrings) {
            val item = deserializeItem(itemStr)
            if (item != null) {
                results.add(item)
            }
        }
        return results
    }

    fun deserializeItem(itemJson: String): SavedCalculation? {
        return try {
            val map = parseSimpleJsonObject(itemJson)
            val id = map["id"] ?: return null
            val projectName = map["projectName"] ?: "Untitled Circle"
            val inputType = CircleInputType.valueOf(map["inputType"] ?: "RADIUS")
            val inputValue = BigDecimal(map["inputValue"] ?: "0")
            val unit = MeasurementUnit.valueOf(map["unit"] ?: "CENTIMETER")
            val radiusMm = BigDecimal(map["radiusMm"] ?: "0")
            val diameterMm = BigDecimal(map["diameterMm"] ?: "0")
            val circumferenceMm = BigDecimal(map["circumferenceMm"] ?: "0")
            val areaMm2 = BigDecimal(map["areaMm2"] ?: "0")
            val timestampMillis = map["timestampMillis"]?.toLongOrNull() ?: System.currentTimeMillis()
            val isFavorite = map["isFavorite"]?.toBooleanStrictOrNull() ?: false

            SavedCalculation(
                id = id,
                projectName = projectName,
                inputType = inputType,
                inputValue = inputValue,
                unit = unit,
                radiusMm = radiusMm,
                diameterMm = diameterMm,
                circumferenceMm = circumferenceMm,
                areaMm2 = areaMm2,
                timestampMillis = timestampMillis,
                isFavorite = isFavorite
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun quote(string: String): String {
        val escaped = string
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        return "\"$escaped\""
    }

    private fun splitJsonArray(jsonArrayStr: String): List<String> {
        val inner = jsonArrayStr.trim().removeSurrounding("[", "]").trim()
        if (inner.isEmpty()) return emptyList()

        val items = mutableListOf<String>()
        var depth = 0
        var inQuotes = false
        var isEscape = false
        val current = StringBuilder()

        for (char in inner) {
            if (isEscape) {
                current.append(char)
                isEscape = false
                continue
            }
            if (char == '\\') {
                current.append(char)
                isEscape = true
                continue
            }
            if (char == '"') {
                inQuotes = !inQuotes
                current.append(char)
                continue
            }
            if (!inQuotes) {
                if (char == '{') {
                    depth++
                } else if (char == '}') {
                    depth--
                } else if (char == ',' && depth == 0) {
                    val item = current.toString().trim()
                    if (item.isNotEmpty()) items.add(item)
                    current.clear()
                    continue
                }
            }
            current.append(char)
        }

        val last = current.toString().trim()
        if (last.isNotEmpty()) items.add(last)
        return items
    }

    private fun parseSimpleJsonObject(jsonObjStr: String): Map<String, String> {
        val inner = jsonObjStr.trim().removeSurrounding("{", "}").trim()
        val result = mutableMapOf<String, String>()
        if (inner.isEmpty()) return result

        var inQuotes = false
        var isEscape = false
        val currentKey = StringBuilder()
        val currentValue = StringBuilder()
        var parsingKey = true

        var i = 0
        while (i < inner.length) {
            val c = inner[i]
            if (isEscape) {
                if (parsingKey) currentKey.append(c) else currentValue.append(c)
                isEscape = false
                i++
                continue
            }
            if (c == '\\') {
                isEscape = true
                i++
                continue
            }
            if (c == '"') {
                inQuotes = !inQuotes
                i++
                continue
            }
            if (!inQuotes) {
                if (c == ':' && parsingKey) {
                    parsingKey = false
                    i++
                    continue
                }
                if (c == ',') {
                    val k = currentKey.toString().trim()
                    val v = currentValue.toString().trim()
                    if (k.isNotEmpty()) {
                        result[k] = unescape(v)
                    }
                    currentKey.clear()
                    currentValue.clear()
                    parsingKey = true
                    i++
                    continue
                }
            }
            if (parsingKey) currentKey.append(c) else currentValue.append(c)
            i++
        }

        val k = currentKey.toString().trim()
        val v = currentValue.toString().trim()
        if (k.isNotEmpty()) {
            result[k] = unescape(v)
        }

        return result
    }

    private fun unescape(s: String): String {
        return s.removeSurrounding("\"")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
    }
}
