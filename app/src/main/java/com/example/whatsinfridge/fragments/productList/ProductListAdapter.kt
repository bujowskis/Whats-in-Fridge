package com.example.whatsinfridge.fragments.productList

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsinfridge.R
import com.example.whatsinfridge.data.model.ProductEntity
import kotlinx.android.synthetic.main.recyclerview_product_layout.view.*
import java.time.LocalDate

class ProductListAdapter(
    private var itemVisibilityInterface: ItemVisibilityInterface
): RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() { // TODO - move on and use a ListAdapter

    private var productList = emptyList<ProductEntity>()
    // For multi selection
    private var multiSelect = false
    private var selectedProducts = arrayListOf<ProductEntity>()
    private var selectedViews = arrayListOf<ProductViewHolder>()
    // Time till expiration
    private val dateToday = LocalDate.now()

    class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_product_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = productList[position]

        holder.itemView.setBackgroundColor(Color.LTGRAY)
        holder.itemView.tvName.text = currentProduct.name
        holder.itemView.tvCategory.text = currentProduct.category
        holder.itemView.tvExpirationDate.text = currentProduct.expirationDate.toString()
        val amountFullString: String = when (currentProduct.amountType) {
            0 -> {
                // pieces (divide by 100)
                "${(currentProduct.amount.toFloat() / 100)}szt."

            }
            1 -> {
                if (currentProduct.amount < 1000) {
                    // grams (explicitly)
                    "${currentProduct.amount}g"
                } else {
                    // kilograms (divide by 1,000)
                    "${(currentProduct.amount.toFloat() / 1000)}kg"
                }
            }
            2 -> {
                if (currentProduct.amount < 1000) {
                    // milliliters (explicitly)
                    "${currentProduct.amount}ml"
                } else {
                    // liters (divide by 1,000)
                    "${(currentProduct.amount.toFloat() / 1000)}l"
                }
            }
            // This should never happen
            else -> {
                "ERROR"
            }
        }
        holder.itemView.tvAmount.text = amountFullString
        // Conditional component, depending on expirationDate and dateToday
        val daysLeft = currentProduct.expirationDate.toEpochDay() - dateToday.toEpochDay()
        // TODO - do that with 'when' (?)
        // TODO - change UI
        if (daysLeft < 0) {
            // Product has expired
            holder.itemView.tvExpirationDays.text = "(po terminie)"
            holder.itemView.tvExpirationDays.setTextColor(Color.RED)
        } else if (daysLeft < 4) {
            // Very close to expiration
            holder.itemView.tvExpirationDays.text = "(dni: $daysLeft)"
            holder.itemView.tvExpirationDays.setTextColor(Color.YELLOW)
        } else if (daysLeft < 8) {
            // Close to expiration
            holder.itemView.tvExpirationDays.text = "(dni: $daysLeft)"
            holder.itemView.tvExpirationDays.setTextColor(Color.GREEN)
        } else {
            // No need to worry
            holder.itemView.tvExpirationDays.text = ""
        }

        holder.itemView.setOnLongClickListener {
            if (!multiSelect) {
                multiSelect = true
                selectProduct(holder, currentProduct)
                // Change visibility of menu items
                itemVisibilityInterface.multiSelectTrue()
            }
            true
        }

        // Handle clicks on specific products from the RecyclerView
        holder.itemView.rowProductLayout.setOnClickListener{
            if (multiSelect) {
                // Logic for multi selection
                selectProduct(holder, currentProduct)
            } else {
                // Go to product's fragment
                // TODO
                val action = ProductListFragmentDirections.actionProductListFragmentToUpdateProductFragment(currentProduct) // Makes it possible to pass clicked object with navigation arguments
                holder.itemView.findNavController().navigate(action)
            }
        }
    }

    // Helper method that handles MultiSelection
    private fun selectProduct(holder: ProductViewHolder, currentProduct: ProductEntity) {
        // Handle selection, based on current state
        if (selectedProducts.contains(currentProduct)) {
            selectedProducts.remove(currentProduct)
            selectedViews.remove(holder)
            holder.itemView.alpha = 1.0f

            if (selectedProducts.isEmpty()) {
                multiSelect = false
                // Change visibility of menu items
                itemVisibilityInterface.multiSelectFalse()
            }
        } else {
            selectedProducts.add(currentProduct)
            selectedViews.add(holder)
            holder.itemView.alpha = 0.3f
        }
    }

    // Operations on the selected data
    fun deleteSelectedProducts() {
        for (view in selectedViews) view.itemView.alpha = 1.0f
        selectedViews.clear()
        itemVisibilityInterface.deleteSelectedProducts(selectedProducts)
        selectedProducts.clear()
        multiSelect = false
        itemVisibilityInterface.multiSelectFalse()
    }
    fun addSelectedProductsToRecipe() {
        // TODO
    }
    fun cancelSelection() {
        selectedProducts.clear()
        multiSelect = false
        itemVisibilityInterface.multiSelectFalse()
        for (view in selectedViews) view.itemView.alpha = 1.0f
        selectedViews.clear()
    }

    fun setData(product: List<ProductEntity>) {
        this.productList = product
        notifyDataSetChanged() // TODO - move on and use the method which skips this
    }

}