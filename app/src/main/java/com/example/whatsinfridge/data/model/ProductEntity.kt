package com.example.whatsinfridge.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "products_table")
data class ProductEntity(
    /**
     * For presentation purpose, two data encodings are supported:
     * // TODO - characters can be used instead of digits to save on encoding space
     *  EAN-13, which contains a single product encoded as follows:
     *      - 1 control digit, which must be equal to 'W'
     *      - 2 digits encoding the name
     *      - 6 digits encoding the expiration date (2 for day, 2 for month, 2 for year)
     *      - 1 digit encoding the category
     *      - 1 digit encoding the amount type (could be evaluated based on the name and type)
     *      - 1 digit encoding the amount
     *  QR code, which contains a list of products encoded as follows:
     *      - control line, which must be equal to "WiF_EAN_13"
     *      - list of products encoded in EAN-13 and a format specified above
     *
     * These implementations are purely exhibition-purpose. An actual app will have to e.g.
     * search a database of products to access all needed values. It's definitely possible.
     *
     * The encodings are considered IDs, based on which a proper ProductEntity is created
     * using a dedicated function decodeEAN_13
     *
     * To check the values assigned to these IDs, see the comments at the end of this .kt file
     */

    val name: String,
    val expirationDate: String, // TODO - change to proper type (Date ?)
    val category: String,
    //val amountType: Int, TODO
    val amount: String // TODO - change to Int

    /* See comment below
    //@Embedded
    //val metadata: ProductMetadata TODO - use separated classes like this
     */
): Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
/* Many more such fields can be added like this
data class ProductMetadata(
    val producent: String,
    val country: String
)
 */

// Explanation of the encodings:
/**
 * Names: TODO
 * 0 ->
 */
/**
 * Expiration dates:
 * day - encoded explicitly
 * month - encoded explicitly
 * year - encoded as the decimal number and the units number, assuming range 2000-2099
 */
/**
 * Categories:
 * 0 -> Vegetables
 * 1 -> Fruits
 * 2 -> Dairy
 * 3 -> Meat
 * 4 -> Fish
 * 5 -> Candy
 * 6 -> Dishes
 * 7 -> Cereals
 * 8 -> Drinks
 * 9 -> Others
 */
/**
 * Amount types:
 * 0 -> pieces
 * 1 -> kilograms
 * 2 -> liters
 */
/**
 * Amounts:
 * 0 -> 50
 * 1 -> 100
 * 2 -> 150
 * 3 -> 200
 * 4 -> 250
 * 5 -> 330
 * 6 -> 500
 * 7 -> 1000
 * 8 -> 1500
 * 9 -> 2000
 * NOTE - amounts are specified as ml and g respectively.
 * In case of products measures in pieces, they are stored such that 100 = 100% = 1 piece
 */

