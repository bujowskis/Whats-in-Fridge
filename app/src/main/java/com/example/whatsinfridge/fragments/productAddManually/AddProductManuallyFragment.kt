package com.example.whatsinfridge.fragments.productAddManually

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.whatsinfridge.R
import com.example.whatsinfridge.data.model.ProductEntity
import com.example.whatsinfridge.data.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.fragment_add_product_manually.*

class AddProductManuallyFragment : Fragment() {

    private lateinit var mProductViewModel: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product_manually, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mProductViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

        btnAdd.setOnClickListener{
            insertNewProduct()
        }

    }

    private fun insertNewProduct() {
        // TODO - proper elements and evaluation
        val name = etName.text.toString()
        val expirationDate = etExpirationDate.text.toString()
        val category = etCategory.text.toString()
        val amount = etAmount.text.toString()

        if (inputCheck(name, expirationDate, category, amount)) {
            val newProduct = ProductEntity(name, expirationDate, category, amount)
            mProductViewModel.addSingleProduct(newProduct)
            Toast.makeText(requireContext(), "Product added", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Not added - all fields must be filled", Toast.LENGTH_SHORT).show()
        }
        findNavController().navigate(R.id.action_addProductManuallyFragment_to_productListFragment)
    }

    private fun inputCheck(name: String, expirationDate: String, category: String, amount: String): Boolean {
        return !(TextUtils.isEmpty(name) && TextUtils.isEmpty(expirationDate) && TextUtils.isEmpty(category) && TextUtils.isEmpty(amount))
    }
}