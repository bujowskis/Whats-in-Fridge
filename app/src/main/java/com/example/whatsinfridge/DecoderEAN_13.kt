package com.example.whatsinfridge

import com.example.whatsinfridge.data.model.ProductEntity
import java.time.LocalDate
import java.time.format.DateTimeParseException

@ExperimentalStdlibApi
object DecoderEAN_13 {
    // TODO - optimize this function
    fun decodeEAN_13(codeString: String): ProductEntity? {
        println("Testing EAN decoding")

        if (codeString.length != 13 && codeString.length != 12) {
            println("improper length")
            return null
        } // This should never happen
        if (codeString[0].toInt() != 0) {
            println("first digit is not equal to 0")
            return null
        }
        val nameId = codeString[1].digitToInt() * 10 + codeString[2].digitToInt()
        val expirationDateString = "20${codeString[3]}${codeString[4]}-${codeString[5]}${codeString[6]}-${codeString[7]}${codeString[8]}"
        val categoryId = codeString[9].digitToInt()
        val amountTypeId = codeString[10].digitToInt()
        val amountId = codeString[11].digitToInt()
        println("nameId: $nameId")
        println("expirationDateString: $expirationDateString")
        println("categoryId: $categoryId")
        println("amountTypeId: $amountTypeId")
        println("amountId: $amountId")

        val name = nameIdToString(nameId)
        if (name == null) return null
        val expirationDate = expirationDateStringToLocalDate(expirationDateString)
        if (expirationDate == null) return null
        val category = categoryIdToString(categoryId)
        if (category == null) return null
        val amountType = amountTypeIdToAmountType(amountTypeId)
        if (amountType == -1) return null
        val amount = amountIdToInt(amountId)
        if (amount == -1) return null

        return ProductEntity(name, expirationDate, category, amountType, amount)
    }

    // To compensate for Zxing taking EAN_13 as UPC_A
    fun decodeMistakenUPC_A(codeString: String): ProductEntity? {
        println("Testing mistaken UPC_A decoding")
        // In this case, the first digit of the EAN_13 is omitted
        // It is a control one, so this workaround is possible
        if (codeString.length != 12) return null

        val nameId = codeString[0].digitToInt() * 10 + codeString[1].digitToInt()
        val expirationDateString = "20${codeString[2]}${codeString[3]}-${codeString[4]}${codeString[5]}-${codeString[6]}${codeString[7]}"
        val categoryId = codeString[8].digitToInt()
        val amountTypeId = codeString[9].digitToInt()
        val amountId = codeString[10].digitToInt()

        println("nameId: $nameId")
        println("expirationDateString: $expirationDateString")
        println("categoryId: $categoryId")
        println("amountTypeId: $amountTypeId")
        println("amountId: $amountId")

        val name = nameIdToString(nameId)
        if (name == null) return null
        val expirationDate = expirationDateStringToLocalDate(expirationDateString)
        if (expirationDate == null) return null
        val category = categoryIdToString(categoryId)
        if (category == null) return null
        val amountType = amountTypeIdToAmountType(amountTypeId)
        if (amountType == -1) return null
        val amount = amountIdToInt(amountId)
        if (amount == -1) return null

        return ProductEntity(name, expirationDate, category, amountType, amount)
    }

    // Converting and validating Ids
    /** Returns null on failure */
    fun nameIdToString(nameId: Int): String? {
        return if (nameId < 0 || nameId > 99) { null } else when (nameId) {
            // TODO
            0 -> "Ogórek"
            1 -> "Papryka czerwona"
            2 -> "Mleko"
            3 -> "Cytryna"
            4 -> "Kalafior"
            5 -> "Ziemniaki"
            6 -> "Pomidor"
            7 -> "Brokuł"
            8 -> "Dorsz"
            9 -> "Kurczak pierś"
            else -> null
        }
    }

    /** Returns null on failure */
    fun expirationDateStringToLocalDate(expirationDateString: String): LocalDate? {
        return try { LocalDate.parse(expirationDateString) } catch (e: DateTimeParseException) { null }
    }

    /** Returns null on failure */
    fun categoryIdToString(categoryId: Int): String? {
        if (categoryId < 0 || categoryId > 9) return null
        return when (categoryId) {
            0 -> "Warzywa"
            1 -> "Owoce"
            2 -> "Nabiał"
            3 -> "Mięso"
            4 -> "Ryby"
            5 -> "Słodycze"
            6 -> "Potrawy"
            7 -> "Zbożowe"
            8 -> "Napoje"
            9 -> "Inne"
            else -> null
        }
    }

    /** Returns -1 on failure */
    fun amountTypeIdToAmountType(amountTypeId: Int): Int {
        // 0 - pieces
        // 1 - grams (kilograms)
        // 2 - milliliters (liters)
        return if (amountTypeId < 0 || amountTypeId > 2) { -1 } else amountTypeId
    }

    /** Returns -1 on failure */
    fun amountIdToInt(amountId: Int): Int {
        return if (amountId < 0 || amountId > 9) { -1 } else when (amountId) {
            0 -> 50
            1 -> 100
            2 -> 150
            3 -> 200
            4 -> 250
            5 -> 330
            6 -> 500
            7 -> 1000
            8 -> 1500
            9 -> 2000
            else -> -1
        }
    }
}