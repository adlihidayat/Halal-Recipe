package com.example.halalrecipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ReviewsFragment : Fragment() {

    private var foodId: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewAdapter: ReviewAdapter
    private val reviewList = mutableListOf<ReviewData>() // List untuk data review

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout fragment_reviews
        val view = inflater.inflate(R.layout.fragment_reviews, container, false)

        // Ambil data yang dikirim melalui arguments
        foodId = arguments?.getString("id") ?: "" // Simpan ke properti kelas
        Log.d("DetailFragment", "Received foodId: $foodId")

        // Setup RecyclerView untuk Review
        recyclerView = view.findViewById(R.id.listReviews)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi adapter dan set ke RecyclerView
        reviewAdapter = ReviewAdapter(requireContext(), reviewList)
        recyclerView.adapter = reviewAdapter

        // Fetch data review dari Firestore
        fetchData()

        // Setup Back Button
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            // Kembali ke fragment sebelumnya
            parentFragmentManager.popBackStack()
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
                }
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
}