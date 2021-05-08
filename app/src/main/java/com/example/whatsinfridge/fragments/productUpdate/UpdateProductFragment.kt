package com.example.whatsinfridge.fragments.productUpdate

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
        etUpdateCategory.setText(args.currentProduct.category)
        etUpdateExpirationDate.setText(instantToExDateString(args.currentProduct.expirationDate))
        when (args.currentProduct.amountType) {
            0 -> {
                // pieces (divide by 100)
                spinnerUpdateAmountType.setSelection(0) // TODO - check if that's the proper index
                etUpdateAmount.setText((args.currentProduct.amount.toFloat() / 100).toString())
            }
            1 -> {
                if (args.currentProduct.amount < 1000) {
                    // grams (explicitly)
                    spinnerUpdateAmountType.setSelection(1) // TODO - check if that's the proper index
                    etUpdateAmount.setText(args.currentProduct.amount)
                } else {
                    // kilograms (divide by 1,000)
                    spinnerUpdateAmountType.setSelection(2) // TODO - check if that's the proper index
                    etUpdateAmount.setText((args.currentProduct.amount.toFloat() / 1000).toString())
                }
            }
            2 -> {
                if (args.currentProduct.amount < 1000) {
                    // milliliters (explicitly)
                    spinnerUpdateAmountType.setSelection(3) // TODO - check if that's the proper index
                    etUpdateAmount.setText(args.currentProduct.amount)
                } else {
                    // liters (divide by 1,000)
                    spinnerUpdateAmountType.setSelection(4) // TODO - check if that's the proper index
                    etUpdateAmount.setText((args.currentProduct.amount.toFloat() / 1000).toString())
                }
            }
        }

        btnUpdate.setOnClickListener {
            updateItem()
        }
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
        val name = etUpdateName.text.toString()
        val category = etUpdateCategory.text.toString()
        val expirationDate = etUpdateExpirationDate.text.toString()
        val amountTypeString = spinnerUpdateAmountType.selectedItem.toString()
        val amount = etUpdateAmount.text

        val updatedProduct: ProductEntity? = inputCheck(name, category, expirationDate, amountTypeString, amount)
        if (updatedProduct != null) {
            mProductViewModel.updateProduct(updatedProduct)
            Toast.makeText(requireContext(), "Zapisano zmiany", Toast.LENGTH_SHORT).show()
        } // Proper Toasts are shown from within inputCheck
        findNavController().navigate(R.id.action_updateProductFragment_to_productListFragment)
    }

    private fun inputCheck(name: String, category: String, expirationDateString: String, amountTypeString: String, amountEditable: Editable): ProductEntity? {
        // Name
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(requireContext(), "Anuluj - nie podano nazwy", Toast.LENGTH_LONG).show()
            return null
        }

        // Category
        if (TextUtils.isEmpty(category)) {
            Toast.makeText(requireContext(), "Anuluj - nie podano kategorii", Toast.LENGTH_LONG)
                .show()
            return null
        }

        // Amount and Amount type
        if (amountEditable.isEmpty()) {
            Toast.makeText(requireContext(), "Anuluj - nie podano ilości", Toast.LENGTH_LONG).show()
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
                    "Anuluj - nie rozpoznano typu wielkości",
                    Toast.LENGTH_LONG
                ).show()
                return null
            }
        }

        // Expiration date
        val expirationDateInstant: Instant
        if (TextUtils.isEmpty(expirationDateString)) {
            Toast.makeText(requireContext(), "Anuluj - nie podano daty ważności", Toast.LENGTH_LONG)
                .show()
            return null
        } else {
            // Validate and parse expirationDate
            expirationDateInstant = LocalDateTime.parse(
                expirationDateString,
                DateTimeFormatter.ofPattern("d.M.uuuu") // TODO - "dd.MM" (?)
            ).atZone(ZoneId.systemDefault()).toInstant() // TODO - get proper zoneId
            if (expirationDateInstant == null) {
                Toast.makeText(
                    requireContext(),
                    "Anuluj - zły format daty ważności",
                    Toast.LENGTH_LONG
                ).show()
                return null
            }
        }
        return ProductEntity(name, expirationDateInstant, category, amountTypeId, amountInt)
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

    private fun instantToExDateString(instant: Instant): String {
        // Returns String of an expiration date in a format accepted by the app
        val formatter = DateTimeFormatter.ofPattern("d.M.uuuu").withZone(ZoneId.systemDefault()) // TODO - proper formatter
        return formatter.format(instant)
    }
}