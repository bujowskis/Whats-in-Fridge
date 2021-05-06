package com.example.whatsinfridge.fragments.productList

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsinfridge.R
import com.example.whatsinfridge.data.model.ProductEntity
import com.example.whatsinfridge.data.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.fragment_product_list.*

class ProductListFragment : Fragment(),
    SearchView.OnQueryTextListener,
    ItemVisibilityInterface {

    private lateinit var mProductViewModel: ProductViewModel
    private val recyclerViewAdapter: ProductListAdapter by lazy { ProductListAdapter(this) }

    // For menu item visibility changing
    private var menuItemSearch: MenuItem? = null
    private var menuItemDeleteAll: MenuItem? = null
    private var menuItemAddRecipe: MenuItem? = null
    private var menuItemCancel: MenuItem? = null
    private var menuItemDeleteSelected: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView
        rvProducts.adapter = recyclerViewAdapter//rvAdapter
        rvProducts.layoutManager = LinearLayoutManager(requireContext())

        // ProductViewModel
        mProductViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        mProductViewModel.readAllData.observe(viewLifecycleOwner, Observer { product ->
            recyclerViewAdapter.setData(product) // Observes changes in DB and invokes appropriate changes
        })

        fabAddNewProduct.setOnClickListener {
            // TODO - show Popup menu where user can choose manual or barcode adding
            findNavController().navigate(R.id.action_productListFragment_to_addProductManuallyFragment)
        }

        // TODO - consider using ActionMode
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // super.onCreateOptionsMenu(menu, inflater) TODO - is needed?
        inflater.inflate(R.menu.main_menu, menu)

        // Implement searching
        val searchMatching = menu.findItem(R.id.item_search)
        val searchMatchingView = searchMatching?.actionView as? SearchView
        searchMatchingView?.isSubmitButtonEnabled = true // Enable submit button if query is not empty
        searchMatchingView?.setOnQueryTextListener(this)

        // Store a reference to the items
        menuItemSearch = menu.findItem(R.id.item_search)
        menuItemDeleteAll = menu.findItem(R.id.item_delete_all)
        menuItemAddRecipe = menu.findItem(R.id.item_add_recipe)
        menuItemCancel = menu.findItem(R.id.item_cancel)
        menuItemDeleteSelected = menu.findItem(R.id.item_delete_selected)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // No need to listen for item_search
            R.id.item_delete_all -> deleteAllProducts()
            R.id.item_add_recipe -> recyclerViewAdapter.addSelectedProductsToRecipe()
            R.id.item_cancel -> recyclerViewAdapter.cancelSelection()
            R.id.item_delete_selected -> recyclerViewAdapter.deleteSelectedProducts()
        }

        return super.onOptionsItemSelected(item)
    }

    // For searching the RecyclerView
    override fun onQueryTextSubmit(query: String?): Boolean {
        // Triggered only if we click submit button in SearchWidget
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        // Triggered every time we put some text in SearchWidget
        if (query != null) {
            searchMatchingProducts(query)
        }
        return true
    }

    // For invoking changes in OptionsMenu, based on multi selection TODO - is this method fine?
    override fun multiSelectTrue() {
        // Show actions on selected products
        menuItemAddRecipe?.isVisible = true
        menuItemCancel?.isVisible = true
        menuItemDeleteSelected?.isVisible = true
        // Hide inapplicable actions
        menuItemDeleteAll?.isVisible = false
    }
    override fun multiSelectFalse() {
        // Hide actions on selected products
        menuItemAddRecipe?.isVisible = false
        menuItemCancel?.isVisible = false
        menuItemDeleteSelected?.isVisible = false
        // Show other applicable actions
        menuItemDeleteAll?.isVisible = true
    }

    override fun deleteSelectedProducts(products: ArrayList<ProductEntity>) {
        for (product in products) mProductViewModel.deleteSingleProduct(product)
    }

    private fun searchMatchingProducts(query: String) {
        // Percentage signs because that's how the query for the SQLite requires it
        val searchQuery = "%$query%"

        mProductViewModel.searchMatchingProducts(searchQuery).observe(this, { list ->
            list.let {
                recyclerViewAdapter.setData(it)
            }
        })
    }

    private fun deleteAllProducts() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Tak") { _, _ ->
            mProductViewModel.deleteAllProducts()
            Toast.makeText(requireContext(), "Usunięto wszystkie produkty", Toast.LENGTH_LONG).show()
        }
        builder.setNegativeButton("Nie") { _, _ ->

        }
        builder.setTitle("Usunąć wszystko?")
        builder.setMessage("Czy na pewno chcesz usunąć wszystkie produkty?")
        builder.create().show()
    }

}