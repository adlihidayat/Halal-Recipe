package com.example.halalrecipe

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class Detail : Fragment() {

    @SuppressLint("MissingInflatedId")

    private var foodId: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewAdapter: ReviewAdapter
    private val reviewList = mutableListOf<ReviewData>() // List untuk data review

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        // Ambil data yang dikirim melalui arguments
        foodId = arguments?.getString("id") ?: "" // Simpan ke properti kelas
        val title = arguments?.getString("title")
        val author = arguments?.getString("author")
        val imageFood = arguments?.getString("imageFood")
        val ingredients = arguments?.getStringArrayList("ingredients")

        // Tampilkan data ke dalam view
        view.findViewById<TextView>(R.id.title).text = title
        view.findViewById<TextView>(R.id.author).text = author
        view.findViewById<TextView>(R.id.ingredients).text = ingredients?.joinToString(", ")

        // Setup RecyclerView untuk Review
        recyclerView = view.findViewById(R.id.listReviews)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi adapter dan set ke RecyclerView
        reviewAdapter = ReviewAdapter(requireContext(), reviewList)
        recyclerView.adapter = reviewAdapter

        // Fetch data review dari Firestore
        fetchData()


        val imageFoodView = view.findViewById<ImageView>(R.id.imageFood)
        Glide.with(this).load(imageFood).into(imageFoodView)

        // Setup Back Button
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            // Kembali ke fragment sebelumnya
            parentFragmentManager.popBackStack()
        }

        // Setup Button untuk menavigasi ke fragment detail
        val navigateButton = view.findViewById<Button>(R.id.all_reviews)
        navigateButton.setOnClickListener {
            // Kirim data menu ke ReviewsFragment
            val menuData = MenuData(foodId)
            navigateToDetail(menuData)
        }

        return view
    }

    private fun fetchData() {
        FirebaseFirestore.getInstance()
            .collection("foods")
            .document(foodId)
            .collection("reviews")
            .get()
            .addOnSuccessListener { result ->
                val newReviewList = result.map { document ->
                    document.toObject(ReviewData::class.java)
                }.take(3)
                reviewList.apply {
                    clear()
                    addAll(newReviewList)
                }
                reviewAdapter.notifyDataSetChanged() // Update RecyclerView
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching reviews: ", exception)
            }
    }

    private fun navigateToDetail(menuData: MenuData) {
        val reviewsFragment = ReviewsFragment()

        // Kirim data melalui arguments
        val bundle = Bundle()
        bundle.putString("id", menuData.id)
        reviewsFragment.arguments = bundle

        // Ganti fragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, reviewsFragment)
            .addToBackStack(null)
            .commit()
    }
}
