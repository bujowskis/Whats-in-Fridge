package com.example.whatsinfridge.fragments.productList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsinfridge.R
import com.example.whatsinfridge.data.model.ProductEntity
import kotlinx.android.synthetic.main.recyclerview_product_layout.view.*

class ProductListAdapter: RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() { // TODO - move on and use a ListAdapter

    private var productList = emptyList<ProductEntity>()

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

        // Handle clicks on specific products from the RecyclerView
        holder.itemView.rowProductLayout.setOnClickListener{
            // TODO - Fragment of a selected product instead of update fragment
            val action = ProductListFragmentDirections.actionProductListFragmentToUpdateProductFragment(currentProduct) // Makes it possible to pass clicked object with navigation arguments
            holder.itemView.findNavController().navigate(action)
        }
    }

    fun setData(product: List<ProductEntity>) {
        this.productList = product
        notifyDataSetChanged() // TODO - move on and use the method which skips this
    }

}