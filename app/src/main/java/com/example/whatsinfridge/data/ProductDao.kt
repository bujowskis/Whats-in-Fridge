package com.example.whatsinfridge.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.whatsinfridge.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

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

}