package com.example.whatsinfridge

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.whatsinfridge.data.model.ProductEntity
import com.example.whatsinfridge.data.viewmodel.ProductViewModel
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity() {

    // TODO - Dependency injection with dagger hilt (?)

    private lateinit var mProductViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBarWithNavController(findNavController(R.id.hostFragment))

        mProductViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
    }

    // Makes it possible to go back using the return arrow of NavController
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.hostFragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Handle scanning the products
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentResult: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents == null) {
                Toast.makeText(this, "Anulowano skan", Toast.LENGTH_SHORT).show()
            } else {
                // TODO - improve by not working on strings (?)
                val formatString = intentResult.formatName.toString()
                val contentsString = intentResult.contents.toString()
                println("podany format: $formatString")
                println("contentsString: $contentsString")
                when (formatString) {
                    "UPC_A" -> {
                        // for some reason, the Zxing confuses EAN_13 with this one
                        val newProduct: ProductEntity? = ProductDecoder.decodeMistakenUPC_A(contentsString)
                        if (newProduct != null) {
                            mProductViewModel.addSingleProduct(newProduct)
                        } else {
                            Toast.makeText(this, "Anuluj - nie rozpoznano produktu", Toast.LENGTH_LONG).show()
                        }
                    }
                    "EAN_13" -> {
                        // Single product encoded in EAN_13
                        val newProduct: ProductEntity? = ProductDecoder.decodeEAN_13(contentsString)
                        if (newProduct != null) {
                            mProductViewModel.addSingleProduct(newProduct)
                        } else {
                            Toast.makeText(this, "Anuluj - nie rozpoznano produktu", Toast.LENGTH_LONG).show()
                        }
                    }
                    "QR_CODE" -> {
                        // (Possibly) list of products encoded in QR Code
                        val productsArrayList = ProductDecoder.decodeQR(contentsString)
                        if (productsArrayList.isEmpty()) {
                            Toast.makeText(this, "Anuluj - nie rozpoznano produktów", Toast.LENGTH_LONG).show()
                        } else {
                            for (product in productsArrayList) mProductViewModel.addSingleProduct(product)
                        }
                    }
                    else -> Toast.makeText(this, "Ten format nie jest obsługiwany", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data) // TODO - is this necessary?
        }
    }
}