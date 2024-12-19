package com.example.halalrecipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

        // Setup RecyclerView
        setupRecyclerViews()

        // Load data kategori Appetizer dan Beverages
        fetchAppetizerData()
        fetchBeverageData()
        fetchDessertData()
        fetchMainCourseData()

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

        // Di dalam onCreateView, setelah inisialisasi view
        val seeAllApp = view.findViewById<TextView>(R.id.seeallapp)
        val seeAllDes = view.findViewById<TextView>(R.id.seealldes)
        val seeAllMain = view.findViewById<TextView>(R.id.seeallmain)
        val seeAllBev = view.findViewById<TextView>(R.id.seeallbev)

// Listener untuk tombol "See all" di appetizer
        seeAllApp.setOnClickListener {
            navigateToCategoryFragment("Appetizer")  // Filter kategori "Appetizer"
        }
        seeAllDes.setOnClickListener {
            navigateToCategoryFragment("Dessert")  // Filter kategori "Appetizer"
        }
        seeAllMain.setOnClickListener {
            navigateToCategoryFragment("Main Course")  // Filter kategori "Appetizer"
        }
        seeAllBev.setOnClickListener {
            navigateToCategoryFragment("Beverages")  // Filter kategori "Appetizer"
        }

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

        // RecyclerView untuk Beverages
        recyclerViewMainCourse.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mainCourseAdapter = MenuAdapter(onItemClick = { menuData ->
            navigateToDetail(menuData)
        })
        recyclerViewMainCourse.adapter = mainCourseAdapter
    }

    private fun fetchAppetizerData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("foods")
            .whereEqualTo("category", "Appetizer") // Ambil data berdasarkan kategori "Appetizer"
            .get()
            .addOnSuccessListener { result ->
                val appetizerList = mutableListOf<MenuData>()
                for (document in result) {
                    val menu = document.toObject(MenuData::class.java).copy(id = document.id)
                    appetizerList.add(menu)
                }
                appetizerAdapter.updateData(appetizerList) // Perbarui adapter dengan data Appetizer
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Gagal mengambil menu appetizer", exception)
            }
    }

    private fun fetchBeverageData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("foods")
            .whereEqualTo("category", "Beverages") // Ambil data berdasarkan kategori "Beverages"
            .get()
            .addOnSuccessListener { result ->
                val beverageList = mutableListOf<MenuData>()
                for (document in result) {
                    val menu = document.toObject(MenuData::class.java).copy(id = document.id)
                    beverageList.add(menu)
                }
                beverageAdapter.updateData(beverageList) // Perbarui adapter dengan data Beverages
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Gagal mengambil menu beverages", exception)
            }
    }

    private fun fetchDessertData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("foods")
            .whereEqualTo("category", "Dessert") // Ambil data berdasarkan kategori "Beverages"
            .get()
            .addOnSuccessListener { result ->
                val dessertList = mutableListOf<MenuData>()
                for (document in result) {
                    val menu = document.toObject(MenuData::class.java).copy(id = document.id)
                    dessertList.add(menu)
                }
                dessertAdapter.updateData(dessertList) // Perbarui adapter dengan data Beverages
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Gagal mengambil menu dessert", exception)
            }
    }

    private fun fetchMainCourseData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("foods")
            .whereEqualTo("category", "Main Course") // Ambil data berdasarkan kategori "Beverages"
            .get()
            .addOnSuccessListener { result ->
                val mainCourseList = mutableListOf<MenuData>()
                for (document in result) {
                    val menu = document.toObject(MenuData::class.java).copy(id = document.id)
                    mainCourseList.add(menu)
                }
                mainCourseAdapter.updateData(mainCourseList) // Perbarui adapter dengan data Beverages
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Gagal mengambil menu Main Course", exception)
            }
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
