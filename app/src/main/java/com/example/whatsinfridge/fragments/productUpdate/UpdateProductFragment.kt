package com.example.whatsinfridge.fragments.productUpdate

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.whatsinfridge.R
import com.example.whatsinfridge.data.model.ProductEntity
import com.example.whatsinfridge.data.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.fragment_update_product.*

class UpdateProductFragment : Fragment() {

    private val args by navArgs<UpdateProductFragmentArgs>()

    private lateinit var mProductViewModel: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mProductViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

        etUpdateName.setText(args.currentProduct.name)
        etUpdateExpirationDate.setText(args.currentProduct.expirationDate)
        etUpdateCategory.setText(args.currentProduct.category)

        btnUpdate.setOnClickListener {
            updateItem()
        }

        // Add menu
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // super.onCreateOptionsMenu(menu, inflater) TODO - is needed?
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_delete) {
            deleteProduct()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateItem() {
        // TODO - proper values for an entry
        val name = etUpdateName.text.toString()
        val expirationDate = etUpdateExpirationDate.text.toString()
        val category = etUpdateCategory.text.toString()
        val amount = etUpdateAmount.text.toString()

        if (inputCheck(name, expirationDate, category)) {
            val updatedProduct = ProductEntity(name, expirationDate, category, amount)
            mProductViewModel.updateProduct(updatedProduct)
            Toast.makeText(requireContext(), "Updated Product", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Did not update - all fields must be filled", Toast.LENGTH_SHORT).show()
        }
        findNavController().navigate(R.id.action_updateProductFragment_to_productListFragment)
    }

    private fun inputCheck(name: String, expirationDate: String, category: String): Boolean {
        return !(TextUtils.isEmpty(name) && TextUtils.isEmpty(expirationDate) && TextUtils.isEmpty(category))
    }

    private fun deleteProduct() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Tak") { _, _ ->
            mProductViewModel.deleteSingleProduct(args.currentProduct)
            Toast.makeText(requireContext(), "Usunięto ${args.currentProduct.name}", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateProductFragment_to_productListFragment)
        }
        builder.setNegativeButton("Nie") { _, _ ->

        }
        builder.setTitle("Usunąć ${args.currentProduct.name}?")
        builder.setMessage("Czy na pewno chcesz usunąć ${args.currentProduct.name}?")
        builder.create().show()
    }

}