package com.example.whatsinfridge.fragments.singleRecipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.whatsinfridge.R
import kotlinx.android.synthetic.main.fragment_single_recipe.*

class SingleRecipeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAddRecipe1.setOnClickListener {
            findNavController().navigate(R.id.action_singleRecipeFragment_to_recipe1Fragment)
        }
    }

}