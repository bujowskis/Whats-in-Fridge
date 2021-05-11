package com.example.whatsinfridge.fragments.productUpdate

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.*
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.whatsinfridge.R
import com.example.whatsinfridge.data.model.ProductEntity
import com.example.whatsinfridge.data.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.fragment_update_product.*
import java.lang.Math.round
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class UpdateProductFragment : Fragment() {

    private val args by navArgs<UpdateProductFragmentArgs>()

    private lateinit var mProductViewModel: ProductViewModel
    private lateinit var currentSpinnerUpdateAmount: Spinner
    private val dateToday = LocalDate.now()

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
        etUpdateExpirationDate.setText(args.currentProduct.expirationDate.toString())
        when (args.currentProduct.amountType) {
            0 -> {
                // pieces (divide by 100)
                spinnerUpdateAmountType.setSelection(0)
                etUpdateAmount.setText((args.currentProduct.amount.toFloat() / 100).toString())
                spinnerUpdateAmountPcs.visibility = View.VISIBLE
                spinnerUpdateAmountPcs.setSelection(2)
                currentSpinnerUpdateAmount = spinnerUpdateAmountPcs
            }
            1 -> {
                if (args.currentProduct.amount < 1000) {
                    // grams (explicitly)
                    spinnerUpdateAmountType.setSelection(1)
                    etUpdateAmount.setText(args.currentProduct.amount.toString())
                    spinnerUpdateAmountGramMl.visibility = View.VISIBLE
                    spinnerUpdateAmountGramMl.setSelection(3)
                    currentSpinnerUpdateAmount = spinnerUpdateAmountGramMl
                } else {
                    // kilograms (divide by 1,000)
                    spinnerUpdateAmountType.setSelection(2)
                    etUpdateAmount.setText((args.currentProduct.amount.toFloat() / 1000).toString())
                    spinnerUpdateAmountKgLiter.visibility = View.VISIBLE
                    spinnerUpdateAmountKgLiter.setSelection(3)
                    currentSpinnerUpdateAmount = spinnerUpdateAmountKgLiter
                }
            }
            2 -> {
                if (args.currentProduct.amount < 1000) {
                    // milliliters (explicitly)
                    spinnerUpdateAmountType.setSelection(3)
                    etUpdateAmount.setText(args.currentProduct.amount.toString())
                    spinnerUpdateAmountGramMl.visibility = View.VISIBLE
                    spinnerUpdateAmountGramMl.setSelection(3)
                    currentSpinnerUpdateAmount = spinnerUpdateAmountGramMl
                } else {
                    // liters (divide by 1,000)
                    spinnerUpdateAmountType.setSelection(4)
                    etUpdateAmount.setText((args.currentProduct.amount.toFloat() / 1000).toString())
                    spinnerUpdateAmountKgLiter.visibility = View.VISIBLE
                    spinnerUpdateAmountKgLiter.setSelection(3)
                    currentSpinnerUpdateAmount = spinnerUpdateAmountKgLiter
                }
            }
        }
        val daysLeft = args.currentProduct.expirationDate.toEpochDay() - dateToday.toEpochDay()
        when {
            daysLeft < 0 -> {
                // Product has expired
                tvTillExpiration.text = "(po terminie)"
            }
            else -> {
                tvTillExpiration.text = "(dni: $daysLeft)"
            }
        } // daysLeft

        // Simple amount adjusting functionality
        // TODO - optimize (roundFloatTo2Decimal too)
        spinnerUpdateAmountType.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        // pieces
                        spinnerUpdateAmountPcs.visibility = View.VISIBLE
                        spinnerUpdateAmountKgLiter.visibility = View.INVISIBLE
                        spinnerUpdateAmountGramMl.visibility = View.INVISIBLE
                        currentSpinnerUpdateAmount = spinnerUpdateAmountPcs
                        spinnerUpdateAmountPcs.setSelection(2)
                    }
                    1 -> {
                        // grams
                        spinnerUpdateAmountPcs.visibility = View.INVISIBLE
                        spinnerUpdateAmountKgLiter.visibility = View.INVISIBLE
                        spinnerUpdateAmountGramMl.visibility = View.VISIBLE
                        spinnerUpdateAmountGramMl.setSelection(3)
                        currentSpinnerUpdateAmount = spinnerUpdateAmountGramMl
                    }
                    2 -> {
                        // kilograms
                        spinnerUpdateAmountPcs.visibility = View.INVISIBLE
                        spinnerUpdateAmountKgLiter.visibility = View.VISIBLE
                        spinnerUpdateAmountGramMl.visibility = View.INVISIBLE
                        spinnerUpdateAmountKgLiter.setSelection(3)
                        currentSpinnerUpdateAmount = spinnerUpdateAmountKgLiter
                    }
                    3 -> {
                        // milliliters
                        spinnerUpdateAmountPcs.visibility = View.INVISIBLE
                        spinnerUpdateAmountKgLiter.visibility = View.INVISIBLE
                        spinnerUpdateAmountGramMl.visibility = View.VISIBLE
                        spinnerUpdateAmountGramMl.setSelection(3)
                        currentSpinnerUpdateAmount = spinnerUpdateAmountGramMl
                    }
                    4 -> {
                        // liters
                        spinnerUpdateAmountPcs.visibility = View.INVISIBLE
                        spinnerUpdateAmountKgLiter.visibility = View.VISIBLE
                        spinnerUpdateAmountGramMl.visibility = View.INVISIBLE
                        spinnerUpdateAmountKgLiter.setSelection(3)
                        currentSpinnerUpdateAmount = spinnerUpdateAmountKgLiter
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        fabAddAmount.setOnClickListener {
            val amountStringNow = etUpdateAmount.text.toString()
            val amountNow: Float = if (amountStringNow.isEmpty()) { 0F } else amountStringNow.toFloat()
            val amountToAdd = currentSpinnerUpdateAmount.selectedItem.toString().toFloat()
            val result = roundFloatTo2Decimal(amountNow + amountToAdd)
            etUpdateAmount.setText("$result")
        }
        fabSubtractAmount.setOnClickListener {
            val amountStringNow = etUpdateAmount.text.toString()
            val amountNow: Float = if (amountStringNow.isEmpty()) { 0F } else amountStringNow.toFloat()
            val amountToSubtract = currentSpinnerUpdateAmount.selectedItem.toString().toFloat()
            val result = roundFloatTo2Decimal(amountNow - amountToSubtract).coerceAtLeast(0F)
            etUpdateAmount.setText("$result")
        }

        btnUpdate.setOnClickListener {
            updateItem()
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
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

        val updatedProduct: ProductEntity? = inputCheckUpdate(name, category, expirationDate, amountTypeString, amount)
        if (updatedProduct != null) {
            if (updatedProduct.amount <= 0) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setPositiveButton("Tak") { _, _ ->
                    mProductViewModel.deleteSingleProduct(args.currentProduct)
                    Toast.makeText(requireContext(), "Usunięto ${args.currentProduct.name}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_updateProductFragment_to_productListFragment)
                }
                builder.setNegativeButton("Nie") { _, _ ->

                }
                builder.setTitle("Usunąć ${args.currentProduct.name}?")
                builder.setMessage("Ta operacja usunie ${args.currentProduct.name}. Kontynuować?")
                builder.create().show()
            } else {
                updatedProduct.id = args.currentProduct.id
                mProductViewModel.updateProduct(updatedProduct)
                Toast.makeText(requireContext(), "Zapisano zmiany", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_updateProductFragment_to_productListFragment)
            }
        }
    }

    private fun inputCheckUpdate(name: String, category: String, expirationDateString: String, amountTypeString: String, amountEditable: Editable): ProductEntity? {
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
        // it's possible for the user to enter half a piece as "0.5", thus Float
        val amountUnconverted = amountEditable.toString().toFloat()
        // amountTypeString will never be empty, as it's retrieved from the spinner
        val amountTypeId: Int
        val amountInt: Int
        when (amountTypeString) {
            // TODO - better way of obtaining Int from the editable
            "szt." -> {
                // 1 piece equals 100 as an integer
                amountTypeId = 0
                amountInt = (amountUnconverted * 100).toInt()
            }
            "g" -> {
                // Amount is given explicitly
                amountTypeId = 1
                amountInt = amountUnconverted.toInt()
            }
            "kg" -> {
                // Amount has to be multiplied by 1,000
                amountTypeId = 1
                amountInt = (amountUnconverted * 1000).toInt()
            }
            "ml" -> {
                // Amount is given explicitly
                amountTypeId = 2
                amountInt = amountUnconverted.toInt()
            }
            "l" -> {
                // Amount has to be multiplied by 1,000
                amountTypeId = 2
                amountInt = (amountUnconverted * 1000).toInt()
            }
            else -> { throw Exception("did not recognize amountType") }
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

    /** Rounds given Float to the second decimal place to compensate for the fraction part */
    private fun roundFloatTo2Decimal(number: Float): Float {
        return (round(number * 100).toFloat() / 100)
    }
}