package com.example.whatsinfridge

import com.example.whatsinfridge.data.model.ProductEntity
import java.time.LocalDate
import java.time.format.DateTimeParseException

@ExperimentalStdlibApi
object ProductDecoder {
    // TODO - optimize this function
    fun decodeEAN_13(codeString: String): ProductEntity? {
        println("Testing EAN decoding")

        if (codeString.length != 13) {
            println("improper length")
            return null
        } // This should never happen
        if (codeString[0].digitToInt() != 0) {
            println("first digit is not equal to 0")
            return null
        }
        val nameId = codeString[1].digitToInt() * 10 + codeString[2].digitToInt()
        val expirationDateString = "20${codeString[3]}${codeString[4]}-${codeString[5]}${codeString[6]}-${codeString[7]}${codeString[8]}"
        val categoryId = codeString[9].digitToInt()
        val amountTypeId = codeString[10].digitToInt()
        val amountId = codeString[11].digitToInt()

        // TODO - remove these printings
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

    // TODO - throw exceptions for more insightful failure recognition
    /** Returns empty ArrayList<ProductEntity?> on failure */
    fun decodeQR(contents: String): ArrayList<ProductEntity> {
        var productsArrayList = arrayListOf<ProductEntity>()

        val contentsList: List<String> = contents.split('\n')
        // WiF_EAN_13
        if (contentsList[0] != "WiF_EAN_13" || contentsList.size < 2 ) return productsArrayList
        for (i in 1 until contentsList.size) {
            val currentProduct: ProductEntity? = decodeEAN_13(contentsList[i])
            if (currentProduct == null) {
                // If at least one product was not decoded, cancel adding
                productsArrayList.clear()
                return productsArrayList
            } else {
                productsArrayList.add(currentProduct)
            }
        }
        return productsArrayList
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

            // 0 Vegetables
            10 -> "Sałata"
            11 -> "Cebula"
            12 -> "Marchew"
            13 -> "Awokado"
            14 -> "Kapusta"
            15 -> "Kukurydza"
            16 -> "Bakłażan"
            17 -> "Szpinak"
            18 -> "Cukinia"
            19 -> "Dynia"

            // 1 Fruits
            20 -> "Jabłka"
            21 -> "Kiwi"
            22 -> "Pomarańcze"
            23 -> "Winogrona"
            24 -> "Banan"
            25 -> "Truskawki"
            26 -> "Ananas"
            27 -> "Borówki"

            // 2 Dairy
            28 -> "Ser żółty"
            29 -> "Mozarella"
            30 -> "Śmietana"
            31 -> "Twaróg"
            32 -> "Jaja"
            33 -> "Feta"
            34 -> "Masło"
            35 -> "Ser pleśniowy"
            36 -> "Jogurt waniliowy"
            37 -> "Jogurt truskawkowy"

            // 3 Meat
            38 -> "Kurczak skrzydełka"
            39 -> "Kiełbasa"
            40 -> "Mięso wieprzowe"
            41 -> "Mięso wołowe"
            42 -> "Żeberka wołowe"
            43 -> "Szynka wieprzowa"

            // 4 Fish, sea food
            44 -> "Łosoś"
            45 -> "Łosoś wędzony"
            46 -> "Pstrąg"
            47 -> "Krewetki"

            // 5 Sweets
            48 -> "Baton czekoladowy"
            49 -> "Lody waniliowe"
            50 -> "Lody czekoladowe"
            51 -> "Lody truskawkowe"

            // 6 Dishes
            52 -> "Gołąbki"
            53 -> "Pulpety"
            54 -> "Hamburger"
            55 -> "Mini pizza"

            // 7 Grains
            56 -> "Makaron"
            57 -> "Mąka"
            58 -> "Ryż"
            59 -> "Chleb"

            // 8 Drinks
            60 -> "Oranżada"
            61 -> "Sok jabłkowy"
            62 -> "Sok pomarańczowy"
            63 -> "Woda gazowana"
            64 -> "Woda niegazowana"
            65 -> "Cola"

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
            4 -> "Ryby, owoce morza"
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