package com.example.whatsinfridge.fragments.recipeList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.whatsinfridge.R
import kotlinx.android.synthetic.main.fragment_recipe_list.*

class RecipeListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        conLayoutRecipe1.setOnClickListener {
            findNavController().navigate(R.id.action_recipeListFragment_to_singleRecipeFragment)
        }

        fabAddRecipe.setOnClickListener {
            // Show popup menu with options for adding new recipes
            val popupAddProduct = PopupMenu(requireContext(), fabAddRecipe)
            val popupAddProductInflater: MenuInflater = popupAddProduct.menuInflater
            popupAddProductInflater.inflate(R.menu.menu_new_recipe, popupAddProduct.menu)
            popupAddProduct.show()
        }
    }

}