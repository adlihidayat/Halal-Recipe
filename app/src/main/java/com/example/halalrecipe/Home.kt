package com.example.halalrecipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Home : Fragment() {

    private lateinit var viewrecAllMenu: RecyclerView
    private lateinit var viewrecAppetizer: RecyclerView
    private lateinit var allMenuAdapter: MenuAdapter
    private lateinit var appetizerAdapter: MenuAdapter
    private lateinit var nameText: TextView
    private lateinit var profileImage: ShapeableImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi RecyclerView
        setupRecyclerViews(view)

        // Inisialisasi nameText dan profileImage
        nameText = view.findViewById(R.id.name)
        profileImage = view.findViewById(R.id.profile)

        // Setup kategori listener
        setupCategoryListeners(view)

        // Ambil data dari Firestore
        fetchAllMenuData()
        fetchAppetizerData()

        // Ambil data pengguna untuk nama dan foto profil
        fetchUserData()

        return view
    }

    private fun setupRecyclerViews(view: View) {
        // RecyclerView untuk semua menu
        viewrecAllMenu = view.findViewById(R.id.viewrec)
        allMenuAdapter = MenuAdapter(
            onItemClick = { menuData -> navigateToDetail(menuData) }, // Pass the onItemClick lambda
            useBiggerLayout = false // Pass false or true based on your requirement
        )
        viewrecAllMenu.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        viewrecAllMenu.adapter = allMenuAdapter

        // RecyclerView untuk menu kategori "Appetizer"
        viewrecAppetizer = view.findViewById(R.id.viewFew)
        appetizerAdapter = MenuAdapter(
            onItemClick = { menuData -> navigateToDetail(menuData) }, // Pass the onItemClick lambda
            useBiggerLayout = false // Pass false or true based on your requirement
        )
        viewrecAppetizer.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        viewrecAppetizer.adapter = appetizerAdapter
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
        bundle.putStringArrayList("tutorial", ArrayList(menuData.tutorial ?: emptyList()))
        detailFragment.arguments = bundle

        // Ganti fragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun fetchUserData() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid  // Ambil ID user yang sedang login

        if (userId != null) {
            db.collection("account").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name")
                        val profileUrl = document.getString("profile")

                        // Update nama pengguna
                        nameText.text = name ?: "User"  // Set default text if name is null

                        // Jika field "profile" berisi URL gambar dari Firebase Storage
                        if (profileUrl.isNullOrEmpty()) {
                            profileImage.setImageResource(R.drawable.pfp)  // Set default image if no profile URL
                        } else {
                            // Mengambil gambar profil dari Firebase Storage menggunakan URL
                            val storageReference = FirebaseStorage.getInstance().reference.child(profileUrl)
                            storageReference.downloadUrl.addOnSuccessListener { uri ->
                                // Setelah URL gambar diambil, tampilkan dengan Glide
                                Glide.with(requireContext())
                                    .load(uri)
                                    .into(profileImage)
                            }
                                .addOnFailureListener { exception ->
                                    Log.e("StorageError", "Error loading profile image", exception)
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Error getting user data", exception)
                }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
