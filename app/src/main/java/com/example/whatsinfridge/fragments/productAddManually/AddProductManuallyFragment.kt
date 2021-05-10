package com.example.whatsinfridge.fragments.productAddManually

import android.os.Bundle
import android.text.Editable
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@ExperimentalStdlibApi
class AddProductManuallyFragment : Fragment() {

    private lateinit var mProductViewModel: ProductViewModel
    private lateinit var productsList: List<ProductEntity>

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

        mProductViewModel.readAllData.observe(viewLifecycleOwner, { products: List<ProductEntity> ->
            // Observes changes in DB and invokes appropriate changes
            productsList = products
        })

        btnAdd.setOnClickListener{
            insertNewProduct()
        }
    }

    private fun insertNewProduct() {
        val name = etName.text.toString()
        val category = etCategory.text.toString()
        val expirationDate = etExpirationDate.text.toString()
        val amountTypeString = spinnerAmountType.selectedItem.toString()
        val amount = etAmount.text

        val newProduct: ProductEntity? = inputCheck(name, category, expirationDate, amountTypeString, amount)
        if (newProduct != null) {
            val matchingProduct = searchMatchingProduct(newProduct, productsList)
            if (matchingProduct == null) {
                mProductViewModel.addSingleProduct(newProduct)
                Toast.makeText(requireContext(), "Dodano produkt", Toast.LENGTH_SHORT).show()
            } else {
                matchingProduct.amount += newProduct.amount
                mProductViewModel.updateProduct(matchingProduct)
                Toast.makeText(requireContext(), "Dodano do istniejącego produktu", Toast.LENGTH_SHORT).show()
            }
            findNavController().navigate(R.id.action_addProductManuallyFragment_to_productListFragment)
        }
    }

    private fun inputCheck(name: String, category: String, expirationDateString: String, amountTypeString: String, amountEditable: Editable): ProductEntity? {
        // Name
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(requireContext(), "Nie podano nazwy", Toast.LENGTH_LONG).show()
            return null
        }

        // Category
        if (TextUtils.isEmpty(category)) {
            Toast.makeText(requireContext(), "Nie podano kategorii", Toast.LENGTH_LONG)
                .show()
            return null
        }

        // Amount and Amount type
        if (amountEditable.isEmpty()) {
            Toast.makeText(requireContext(), "Nie podano ilości", Toast.LENGTH_LONG).show()
            return null
        }
        // amountTypeString will never be empty, as it's retrieved from the spinner
        val amountTypeId: Int
        val amountInt: Int
        when (amountTypeString) {
            // TODO - better way of obtaining Int from the editable
            "szt." -> {
                // 1 piece equals 100 as an integer
                // it's possible for the user to enter half a piece as "0.5", thus Float
                amountTypeId = 0
                amountInt = (amountEditable.toString().toFloat() * 100).toInt()
            }
            "g" -> {
                // Amount is given explicitly
                amountTypeId = 1
                amountInt = amountEditable.toString().toInt()
            }
            "kg" -> {
                // Amount has to be multiplied by 1,000
                amountTypeId = 1
                amountInt = (amountEditable.toString().toFloat() * 1000).toInt()
            }
            "ml" -> {
                // Amount is given explicitly
                amountTypeId = 2
                amountInt = amountEditable.toString().toInt()
            }
            "l" -> {
                // Amount has to be multiplied by 1,000
                amountTypeId = 2
                amountInt = (amountEditable.toString().toFloat() * 1000).toInt()
            }
            // This will never happen
            else -> {
                Toast.makeText(
                    requireContext(),
                    "Nie rozpoznano typu wielkości",
                    Toast.LENGTH_LONG
                ).show()
                return null
            }
        }

        // Expiration date
        val expirationDateLocalDate: LocalDate?
        if (TextUtils.isEmpty(expirationDateString)) {
            Toast.makeText(requireContext(), "Nie podano daty ważności", Toast.LENGTH_LONG).show()
            return null
        } else {
            // Validate and parse expirationDate
            expirationDateLocalDate = tryParseStringToLocalDate(expirationDateString)
            if (expirationDateLocalDate == null) {
                Toast.makeText(requireContext(), "Niepoprawny format daty ważności", Toast.LENGTH_LONG
                ).show()
                return null
            }
        }
        return ProductEntity(name, expirationDateLocalDate, category, amountTypeId, amountInt)
    }

    private fun tryParseStringToLocalDate(string: String): LocalDate? {
        return try { LocalDate.parse(string, DateTimeFormatter.ofPattern("yyyy-M-d")) } catch (e: DateTimeParseException) {
            try { LocalDate.parse(string, DateTimeFormatter.ofPattern("d-M-yyyy")) } catch (e: DateTimeParseException) {
                try { LocalDate.parse(string, DateTimeFormatter.ofPattern("yyyy.M.d")) } catch (e: DateTimeParseException) {
                    try { LocalDate.parse(string, DateTimeFormatter.ofPattern("d.M.yyyy")) } catch (e: DateTimeParseException) {
                        try { LocalDate.parse(string, DateTimeFormatter.ofPattern("yyyy/M/d")) } catch (e: DateTimeParseException) {
                            try { LocalDate.parse(string, DateTimeFormatter.ofPattern("d/M/yyyy")) } catch (e: DateTimeParseException) { null }
                        }
                    }
                }
            }
        }
    }

    /** Returns matching product on success, otherwise null */
    private fun searchMatchingProduct(thisProduct: ProductEntity, products: List<ProductEntity>): ProductEntity? {
        // TODO - optimize this process
        for (product in products) {
            if (thisProduct.amountType == product.amountType
                && thisProduct.name.lowercase() == product.name.lowercase()
                && thisProduct.category.lowercase() == product.category.lowercase()
                && thisProduct.expirationDate == product.expirationDate
            ) { return product }
        }
        return null
    }
}