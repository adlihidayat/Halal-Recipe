package com.example.halalrecipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Search : Fragment() {

    private lateinit var recyclerViewAppetizer: RecyclerView
    private lateinit var recyclerViewBeverages: RecyclerView
    private lateinit var recyclerViewDessert: RecyclerView
    private lateinit var recyclerViewMainCourse: RecyclerView

    private lateinit var appetizerAdapter: MenuAdapter
    private lateinit var beverageAdapter: MenuAdapter
    private lateinit var dessertAdapter: MenuAdapter
    private lateinit var mainCourseAdapter: MenuAdapter

    private var allMenuData: MutableList<MenuData> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Inisialisasi RecyclerView
        recyclerViewAppetizer = view.findViewById(R.id.viewrecapp)
        recyclerViewBeverages = view.findViewById(R.id.viewrecbev)
        recyclerViewDessert = view.findViewById(R.id.viewrecdessert)
        recyclerViewMainCourse = view.findViewById(R.id.viewrecmain)

        // Inisialisasi SearchView
        val searchView = view.findViewById<SearchView>(R.id.searchView)

        // Setup listener untuk pencarian
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterData(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterData(it) }
                return true
            }
        })

        // Setup RecyclerViews
        setupRecyclerViews()

        // Fetch data
        fetchAllDataForSearch()

        return view
    }

    private fun setupRecyclerViews() {
        // RecyclerView untuk Appetizer
        recyclerViewAppetizer.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        appetizerAdapter = MenuAdapter(onItemClick = { menuData ->
            navigateToDetail(menuData)
        })
        recyclerViewAppetizer.adapter = appetizerAdapter

        // RecyclerView untuk Beverages
        recyclerViewBeverages.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        beverageAdapter = MenuAdapter(onItemClick = { menuData ->
            navigateToDetail(menuData)
        })
        recyclerViewBeverages.adapter = beverageAdapter

        // RecyclerView untuk Dessert
        recyclerViewDessert.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        dessertAdapter = MenuAdapter(onItemClick = { menuData ->
            navigateToDetail(menuData)
        })
        recyclerViewDessert.adapter = dessertAdapter

        // RecyclerView untuk Main Course
        recyclerViewMainCourse.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mainCourseAdapter = MenuAdapter(onItemClick = { menuData ->
            navigateToDetail(menuData)
        })
        recyclerViewMainCourse.adapter = mainCourseAdapter
    }

    private fun fetchAllDataForSearch() {
        val db = FirebaseFirestore.getInstance()
        db.collection("foods")
            .get()
            .addOnSuccessListener { result ->
                allMenuData.clear()
                for (document in result) {
                    val menu = document.toObject(MenuData::class.java).copy(id = document.id)
                    allMenuData.add(menu)
                }
                // Memperbarui data pada semua kategori
                appetizerAdapter.updateData(allMenuData.filter { it.category == "Appetizer" })
                beverageAdapter.updateData(allMenuData.filter { it.category == "Beverages" })
                dessertAdapter.updateData(allMenuData.filter { it.category == "Dessert" })
                mainCourseAdapter.updateData(allMenuData.filter { it.category == "Main Course" })
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Gagal mengambil semua data", exception)
            }
    }

    private fun filterData(query: String) {
        val filteredList = allMenuData.filter {
            it.title?.contains(query, ignoreCase = true) ?: false
        }

        // Update semua adapter dengan hasil filter
        appetizerAdapter.updateData(filteredList.filter { it.category == "Appetizer" })
        beverageAdapter.updateData(filteredList.filter { it.category == "Beverages" })
        dessertAdapter.updateData(filteredList.filter { it.category == "Dessert" })
        mainCourseAdapter.updateData(filteredList.filter { it.category == "Main Course" })
    }

    private fun navigateToDetail(menuData: MenuData) {
        val detailFragment = Detail()
        val bundle = Bundle().apply {
            putString("id", menuData.id)
            putString("title", menuData.title)
            putString("author", menuData.author)
            putString("imageFood", menuData.imageFood)
            putStringArrayList("ingredients", ArrayList(menuData.ingredients ?: emptyList()))
            putStringArrayList("tutorial", ArrayList(menuData.tutorial ?: emptyList()))
        }
        detailFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }
}
