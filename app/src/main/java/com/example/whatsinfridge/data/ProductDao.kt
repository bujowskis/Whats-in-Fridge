package com.example.whatsinfridge.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.whatsinfridge.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // TODO - use 'upsert' instead of 'input' for both entry creation and update (?)

    // TODO - filtered Queries for displaying categorized data
    // TODO - update info about a product (e.g. 500g -> 200g)
    // TODO - insert product into database by scanning a demo-encoded barcode
    // TODO - insert multiple products into database by scanning a single demo-encoded barcode
    // TODO - delete a single product from the database

    @Insert // TODO - onConflict strategy
    suspend fun addSingleProduct(product: ProductEntity)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteSingleProduct(product: ProductEntity)

    @Query("DELETE FROM products_table")
    suspend fun deleteAllProducts()

    @Query("SELECT * FROM products_table ORDER BY id ASC")
    fun readAllProducts(): LiveData<List<ProductEntity>>

    // TODO - do this with LiveData directly
    @Query("SELECT * FROM products_table WHERE name LIKE :searchQuery OR category LIKE :searchQuery")
    fun searchMatchingProducts(searchQuery: String): Flow<List<ProductEntity>>

    // TODO - make this work with different formatting, e.g. Fish == fish == FISH etc.
    //@Query("SELECT * FROM products_table WHERE name LIKE :product.")
    //fun searchMatchingProductId(product: ProductEntity): Int
}