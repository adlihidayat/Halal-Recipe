package com.example.halalrecipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Home : Fragment() {

    private lateinit var viewrecAllMenu: RecyclerView
    private lateinit var viewrecAppetizer: RecyclerView
    private lateinit var allMenuAdapter: MenuAdapter
    private lateinit var appetizerAdapter: MenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout untuk fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi RecyclerView untuk semua menu
        viewrecAllMenu = view.findViewById(R.id.viewrec)
        allMenuAdapter = MenuAdapter { menuData ->
            navigateToDetail(menuData)
        }
        viewrecAllMenu.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        viewrecAllMenu.adapter = allMenuAdapter

        // Inisialisasi RecyclerView untuk kategori "Appetizer"
        viewrecAppetizer = view.findViewById(R.id.viewFew)
        appetizerAdapter = MenuAdapter { menuData ->
            navigateToDetail(menuData)
        }
        viewrecAppetizer.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        viewrecAppetizer.adapter = appetizerAdapter

        // Ambil data dari Firestore
        fetchAllMenuData()
        fetchAppetizerData()

        return view
    }

    // Ambil data untuk semua menu
    private fun fetchAllMenuData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("foods")
            .get()
            .addOnSuccessListener { result ->
                val allMenuList = mutableListOf<MenuData>()
                for (document in result) {
                    val menu = document.toObject(MenuData::class.java).copy(id = document.id)
                    allMenuList.add(menu)
                }
                allMenuAdapter.updateData(allMenuList) // Perbarui data adapter
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Gagal mengambil semua menu", exception)
            }
    }

    // Ambil data hanya untuk kategori "Appetizer"
    private fun fetchAppetizerData() {
        val db = FirebaseFirestore.getInstance()

        db.collection("foods")
            .whereEqualTo("category", "Appetizer") // Filter berdasarkan kategori
            .get()
            .addOnSuccessListener { result ->
                val appetizerList = mutableListOf<MenuData>()
                for (document in result) {
                    val menu = document.toObject(MenuData::class.java).copy(id = document.id)
                    appetizerList.add(menu)
                }
                appetizerAdapter.updateData(appetizerList) // Perbarui data adapter
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Gagal mengambil menu appetizer", exception)
            }
    }

    private fun navigateToDetail(menuData: MenuData) {
        val detailFragment = Detail()

        // Kirim data melalui arguments
        val bundle = Bundle()
        bundle.putString("id", menuData.id)
        bundle.putString("title", menuData.title)
        bundle.putString("author", menuData.author)
        bundle.putString("imageFood", menuData.imageFood)
        bundle.putStringArrayList("ingredients", ArrayList(menuData.ingredients ?: emptyList()))
        detailFragment.arguments = bundle

        // Ganti fragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }
}
