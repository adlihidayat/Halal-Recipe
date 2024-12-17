package com.example.halalrecipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

        // Setup Button Filter untuk setiap bintang
        setupStarButtons(view)

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

    private fun setupStarButtons(view: View) {
        val allButton = view.findViewById<Button>(R.id.all)
        val star5Button = view.findViewById<Button>(R.id.star5)
        val star4Button = view.findViewById<Button>(R.id.star4)
        val star3Button = view.findViewById<Button>(R.id.star3)
        val star2Button = view.findViewById<Button>(R.id.star2)
        val star1Button = view.findViewById<Button>(R.id.star1)

        // All Button
        allButton.setOnClickListener {
            fetchData() // Menampilkan semua ulasan
        }

        // Filter berdasarkan rating bintang 5
        star5Button.setOnClickListener {
            fetchDataFilter(rating = 5f)
        }

        // Filter berdasarkan rating bintang 4
        star4Button.setOnClickListener {
            fetchDataFilter(rating = 4f)
        }

        // Filter berdasarkan rating bintang 3
        star3Button.setOnClickListener {
            fetchDataFilter(rating = 3f)
        }

        // Filter berdasarkan rating bintang 2
        star2Button.setOnClickListener {
            fetchDataFilter(rating = 2f)
        }

        // Filter berdasarkan rating bintang 1
        star1Button.setOnClickListener {
            fetchDataFilter(rating = 1f)
        }
    }

    private fun fetchDataFilter(rating: Float) {
        // Mengambil data review dari Firestore
        FirebaseFirestore.getInstance()
            .collection("foods")
            .document(foodId)
            .collection("reviews")
            .get()
            .addOnSuccessListener { result ->
                val newReviewList = result.map { document ->
                    document.toObject(ReviewData::class.java)
                }

                // Jika rating dipilih, filter ulasan berdasarkan rating
                val filteredReviews = if (rating != null) {
                    newReviewList.filter { it.rating == rating }
                } else {
                    newReviewList
                }

                reviewList.apply {
                    clear()
                    addAll(filteredReviews)
                }
                reviewAdapter.notifyDataSetChanged() // Update RecyclerView
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching reviews: ", exception)
            }
    }
}
