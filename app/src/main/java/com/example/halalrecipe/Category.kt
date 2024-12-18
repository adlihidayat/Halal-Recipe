package com.example.halalrecipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Category : Fragment() {

    private lateinit var categoryTitle: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuAdapter

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

        // Ambil data kategori dari Bundle
        arguments?.let {
            categoryName = it.getString("CATEGORY_NAME")
        }

        // Set judul kategori
        categoryTitle.text = categoryName ?: "Category"

        // Setup RecyclerView
        setupRecyclerView()

        // Load data sesuai kategori dari Firestore
        loadData(categoryId)

        // Setup Back Button
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            // Kembali ke fragment sebelumnya
            parentFragmentManager.popBackStack()
        }

        // Setup klik untuk navigasi ke kategori
        view.findViewById<View>(R.id.appetizer).setOnClickListener {
            navigateToCategoryFragment("Appetizer")
        }

        view.findViewById<View>(R.id.beverages).setOnClickListener {
            navigateToCategoryFragment("Beverages")
        }

        view.findViewById<View>(R.id.dessert).setOnClickListener {
            navigateToCategoryFragment("Dessert")
        }

        view.findViewById<View>(R.id.mainCourse).setOnClickListener {
            navigateToCategoryFragment("Main Course")
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

    private fun navigateToCategoryFragment(category: String) {
        val categoryFragment = Category()
        val bundle = Bundle().apply {
            putString("CATEGORY_NAME", category)
        }
        categoryFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, categoryFragment)
            .addToBackStack(null)
            .commit()
    }

}
