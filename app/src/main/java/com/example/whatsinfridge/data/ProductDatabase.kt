package com.example.whatsinfridge.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.whatsinfridge.data.model.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
) abstract class ProductDatabase: RoomDatabase() {

    // Returns the Dao
    abstract fun productDao(): ProductDao

    companion object {
        // So that there is only one instance of the database
        @Volatile
        private var INSIANCE: ProductDatabase? = null

        fun getDatabase(context: Context): ProductDatabase {
            val tempInstance = INSIANCE
            if (tempInstance != null) {
                return tempInstance
            }
            // Means of protection from concurrent threads TODO - change synchronized for something better
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    "product_database"
                ).build()
                INSIANCE = instance
                return instance
            }
        }
    }

}