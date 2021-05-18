package com.example.whatsinfridge.fragments.shoppingList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.whatsinfridge.R
import kotlinx.android.synthetic.main.fragment_shopping_list.*


class ShoppingListFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conViewSL1.setOnClickListener {
            findNavController().navigate(R.id.action_shoppingListFragment_to_singleShoppingListFragment)
        }
    }

}