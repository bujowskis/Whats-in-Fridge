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
import com.example.whatsinfridge.data.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.fragment_product_list.*

class ProductListFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var mProductViewModel: ProductViewModel

    // To make the adapter global for the fragment TODO - migrate RecyclerView Adapter here completely
    private val recyclerViewAdapter: ProductListAdapter by lazy { ProductListAdapter() }

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
        //val rvAdapter = ProductListAdapter()
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

        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // super.onCreateOptionsMenu(menu, inflater) TODO - is needed?
        inflater.inflate(R.menu.main_menu, menu)

        // Implement searching as the menu option
        val searchMatching = menu.findItem(R.id.item_search)
        val searchMatchingView = searchMatching?.actionView as? SearchView
        searchMatchingView?.isSubmitButtonEnabled = true // Enable submit button if query is not empty
        searchMatchingView?.setOnQueryTextListener(this)

        //return true TODO - is needed?
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_delete) {
            deleteAllProducts()
        }
        if (item.itemId == R.id.item_search) {

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