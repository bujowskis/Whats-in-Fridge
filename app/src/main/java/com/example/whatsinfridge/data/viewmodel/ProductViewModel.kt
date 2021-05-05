package com.example.whatsinfridge.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.whatsinfridge.data.ProductDatabase
import com.example.whatsinfridge.data.model.ProductEntity
import com.example.whatsinfridge.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<ProductEntity>>
    private val repository: ProductRepository

    init {
        val productDao = ProductDatabase.getDatabase(application).productDao()
        repository = ProductRepository(productDao)
        readAllData = repository.readAllProducts
    }

    fun addSingleProduct(product: ProductEntity) {
        /*
            Dispatchers.IO - makes coroutine run in the background thread
            It's a bad practice to run database coroutines in the main thread
            They should be launched in the background instead
         */
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSingleProduct(product)
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProduct(product)
        }
    }

    fun deleteSingleProduct(product: ProductEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSingleProduct(product)
        }
    }

    fun deleteAllProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllProducts()
        }
    }

    fun searchMatchingProducts(searchQuery: String): LiveData<List<ProductEntity>> {
        // TODO - do this with LiveData directly
        return repository.searchMatchingProducts(searchQuery).asLiveData() // TODO - can it be done without returning?
    }

}