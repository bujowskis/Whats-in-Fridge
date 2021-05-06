package com.example.whatsinfridge.fragments.productList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsinfridge.R
import com.example.whatsinfridge.data.model.ProductEntity
import kotlinx.android.synthetic.main.recyclerview_product_layout.view.*

class ProductListAdapter(private var itemVisibilityInterface: ItemVisibilityInterface): RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() { // TODO - move on and use a ListAdapter

    private var productList = emptyList<ProductEntity>()
    // For multi selection
    private var multiSelect = false
    private var selectedProducts = arrayListOf<ProductEntity>()

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_product_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        // TODO - all needed values for a product entry
        val currentProduct = productList[position]
        holder.itemView.tvId.text = currentProduct.id.toString()
        holder.itemView.tvName.text = currentProduct.name
        holder.itemView.tvExpirationDate.text = currentProduct.expirationDate
        holder.itemView.tvCategory.text = currentProduct.category

        holder.itemView.setOnLongClickListener {
            if (!multiSelect) {
                multiSelect = true
                selectProduct(holder, currentProduct)
                // Change visibility of menu items
                // TODO
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
    private fun selectProduct(holder: ProductListAdapter.ProductViewHolder, currentProductSelection: ProductEntity) {
        // Handle selection, based on current state
        if (selectedProducts.contains(currentProductSelection)) {
            selectedProducts.remove(currentProductSelection)
            holder.itemView.alpha = 1.0f
            if (selectedProducts.isEmpty()) {
                multiSelect = false
                // Change visibility of menu items
                // TODO
                itemVisibilityInterface.multiSelectFalse()
            }
        } else {
            selectedProducts.add(currentProductSelection)
            holder.itemView.alpha = 0.3f
        }
    }

    fun setData(product: List<ProductEntity>) {
        this.productList = product
        notifyDataSetChanged() // TODO - move on and use the method which skips this
    }

}