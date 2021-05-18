package com.example.whatsinfridge.fragments.financesMain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.whatsinfridge.R
import kotlinx.android.synthetic.main.fragment_finances_main.*

class FinancesMainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finances_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabFinancesOptions.setOnClickListener {
            // Show popup menu with options for adding new recipes
            val popupAddProduct = PopupMenu(requireContext(), fabFinancesOptions)
            val popupAddProductInflater: MenuInflater = popupAddProduct.menuInflater
            popupAddProductInflater.inflate(R.menu.menu_finances, popupAddProduct.menu)
            popupAddProduct.show()
        }
    }

}