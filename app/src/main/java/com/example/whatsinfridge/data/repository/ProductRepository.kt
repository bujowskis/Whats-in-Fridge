package com.example.whatsinfridge.data.repository

import androidx.lifecycle.LiveData
import com.example.whatsinfridge.data.ProductDao
import com.example.whatsinfridge.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    val readAllProducts: LiveData<List<ProductEntity>> = productDao.readAllProducts()

    suspend fun addSingleProduct(product: ProductEntity) {
        productDao.addSingleProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    suspend fun deleteSingleProduct(product: ProductEntity) {
        productDao.deleteSingleProduct(product)
    }

    suspend fun deleteAllProducts() {
        productDao.deleteAllProducts()
    }

    // TODO - do this with LiveData directly
    fun searchMatchingProducts(searchQuery: String): Flow<List<ProductEntity>> {
        return productDao.searchMatchingProducts(searchQuery)
    }

}