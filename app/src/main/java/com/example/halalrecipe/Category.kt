package com.example.halalrecipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.SearchView // Tambahkan impor ini jika belum ada

class Category : Fragment() {

    private lateinit var categoryTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuAdapter
    private lateinit var searchView: SearchView // Deklarasi SearchView

    private var categoryName: String? = null
    private var categoryId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        // Inisialisasi View
        categoryTitle = view.findViewById(R.id.categorytitle)
        recyclerView = view.findViewById(R.id.viewmenu)
        searchView = view.findViewById(R.id.searchView) // Inisialisasi SearchView

        // Ambil data kategori dari Bundle
        arguments?.let {
            categoryName = it.getString("CATEGORY_NAME")
        }

        // Set judul kategori
        categoryTitle.text = categoryName ?: "Category"

        // Setup RecyclerView
        setupRecyclerView()

        setupCategoryListeners(view)


        // Load data sesuai kategori dari Firestore
        loadData(categoryId)

        // Setup SearchView
        setupSearchView()

        // Setup Back Button
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            // Kembali ke fragment sebelumnya
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )

        // Inisialisasi adapter dengan callback item click dan layout yang lebih besar
        adapter = MenuAdapter(onItemClick = { menuData ->
            // Navigasi ke DetailFragment saat item diklik
            navigateToDetail(menuData)
        }, useBiggerLayout = true) // Gunakan layout yang lebih besar (viewholder_menubigger)

        recyclerView.adapter = adapter
    }

    private fun setupSearchView() {
        // Tambahkan listener untuk SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true // Tidak memerlukan aksi saat submit
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter data berdasarkan query
                adapter.filterData(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupCategoryListeners(view: View) {
        val appetizer = view.findViewById<FrameLayout>(R.id.appetizer)
        val mainCourse = view.findViewById<FrameLayout>(R.id.mainCourse)
        val dessert = view.findViewById<FrameLayout>(R.id.dessert)
        val beverages = view.findViewById<FrameLayout>(R.id.beverages)

        appetizer.setOnClickListener { navigateToCategoryFragment("Appetizer") }
        mainCourse.setOnClickListener { navigateToCategoryFragment("Main Course") }
        dessert.setOnClickListener { navigateToCategoryFragment("Dessert") }
        beverages.setOnClickListener { navigateToCategoryFragment("Beverages") }
    }

    private fun navigateToCategoryFragment(category: String) {
        val categoryFragment = Category()

        // Kirim data kategori ke fragment Category melalui arguments
        val bundle = Bundle().apply {
            putString("CATEGORY_NAME", category)
        }
        categoryFragment.arguments = bundle

        // Ganti fragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, categoryFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun loadData(categoryId: String?) {
        // Panggil fetchData dari adapter untuk mengambil data sesuai kategori
        categoryName?.let {
            adapter.fetchData(collection = "foods", category = it) // Filter berdasarkan kategori
        }
    }

    // Fungsi untuk berpindah ke DetailFragment
    private fun navigateToDetail(menuData: MenuData) {
        val detailFragment = Detail()

        // Kirim data melalui Bundle
        val bundle = Bundle()
        bundle.putString("id", menuData.id)
        bundle.putString("title", menuData.title)
        bundle.putString("author", menuData.author)
        bundle.putString("imageFood", menuData.imageFood)
        bundle.putStringArrayList("ingredients", ArrayList(menuData.ingredients ?: emptyList()))
        bundle.putStringArrayList("tutorial", ArrayList(menuData.tutorial ?: emptyList()))
        detailFragment.arguments = bundle

        // Ganti fragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }
}
