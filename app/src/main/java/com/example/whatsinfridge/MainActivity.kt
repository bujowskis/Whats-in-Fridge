package com.example.whatsinfridge

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class MainActivity : AppCompatActivity() {

    // TODO - Dependency injection with dagger hilt (?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBarWithNavController(findNavController(R.id.hostFragment))
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
                // TODO - handle data based on the format
                val formatString = intentResult.formatName.toString()
                val contentsString = intentResult.contents.toString()

                println("format kodu: $formatString")
                when (formatString) {
                    // TODO - improve by not working on strings
                    /*
                    "UPC_E" -> {
                        // Single product encoded in UPC_E
                        // TODO
                        Toast.makeText(this, "Rozpoznano UPC-E\ncontents: $contentsString", Toast.LENGTH_LONG).show()
                    }
                     */
                    "EAN_13" -> {
                        // Single product encoded in EAN_13
                        // TODO
                        Toast.makeText(this, "Rozpoznano EAN-13\ncontents: $contentsString", Toast.LENGTH_LONG).show()
                    }
                    "QR_CODE" -> {
                        // (Possibly) list of products encoded in QR Code
                        // TODO
                        Toast.makeText(this, "Rozpoznano QR Code\ncontents: $contentsString", Toast.LENGTH_LONG).show()
                    }
                    else -> Toast.makeText(this, "Format nie jest obsługiwany", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data) // TODO - is this necessary?
        }
    }
}