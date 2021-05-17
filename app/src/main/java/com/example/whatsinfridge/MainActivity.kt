package com.example.whatsinfridge

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.whatsinfridge.data.model.ProductEntity
import com.example.whatsinfridge.data.viewmodel.ProductViewModel
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_main.*

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity() {

    // TODO - Dependency injection with dagger hilt (?)

    private lateinit var mProductViewModel: ProductViewModel
    lateinit var productsList: List<ProductEntity>

    // Navigation drawer
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navOnDestinationChangedListener: NavController.OnDestinationChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigation Drawer TODO - make the drawer close after second burger icon click
        navController = findNavController(R.id.hostFragment)
        drawerLayout = findViewById(R.id.drawerLayoutMain)
        navViewMain.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // TODO - is needed?
        navOnDestinationChangedListener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.fragmentProductList) {

            }
        }

        mProductViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        mProductViewModel.readAllData.observe(this, { products: List<ProductEntity> ->
            // Observes changes in DB and invokes appropriate changes
            productsList = products
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.hostFragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    // TODO - is needed? (navDestChangedListener)
    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(navOnDestinationChangedListener)
    }
    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(navOnDestinationChangedListener)
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
                            val matchingProduct = searchMatchingProduct(newProduct, productsList)
                            if (matchingProduct == null) {
                                mProductViewModel.addSingleProduct(newProduct)
                                Toast.makeText(this, "Dodano produkt", Toast.LENGTH_SHORT).show()
                            } else {
                                matchingProduct.amount += newProduct.amount
                                mProductViewModel.updateProduct(matchingProduct)
                                Toast.makeText(this, "Dodano do istniejącego produktu", Toast.LENGTH_SHORT).show()
                            }
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
                            for (product in productsArrayList) {
                                val matchingProduct = searchMatchingProduct(product, productsList)
                                if (matchingProduct == null) {
                                    mProductViewModel.addSingleProduct(product)
                                } else {
                                    matchingProduct.amount += product.amount
                                    mProductViewModel.updateProduct(matchingProduct)
                                }
                            }
                            Toast.makeText(this, "Dodano produkty", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> Toast.makeText(this, "Ten format nie jest obsługiwany", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data) // TODO - is this necessary?
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