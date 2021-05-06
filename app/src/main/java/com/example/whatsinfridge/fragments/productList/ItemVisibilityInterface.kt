package com.example.whatsinfridge.fragments.productList

import com.example.whatsinfridge.data.model.ProductEntity

// Interface that handles visibility changes
interface ItemVisibilityInterface {
    // Invoking according functionality changes
    fun multiSelectTrue()
    fun multiSelectFalse()

    // Operations on multi selected products
    fun deleteSelectedProducts(products: ArrayList<ProductEntity>)
}