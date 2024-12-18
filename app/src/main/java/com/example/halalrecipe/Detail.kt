package com.example.halalrecipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class Detail : Fragment() {

    private var foodId: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewAdapter: ReviewAdapter
    private val reviewList = mutableListOf<ReviewData>() // List untuk data review

    private lateinit var ratingBar: RatingBar
    private lateinit var comment: EditText
    private lateinit var postButton: Button



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




        // Siapkan data untuk dikirim

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

        // Initialize the new input views
        ratingBar = view.findViewById(R.id.ratingBar)
        comment = view.findViewById(R.id.comment)
        postButton = view.findViewById(R.id.postButton)


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

        val postButton = view.findViewById<Button>(R.id.postButton)
        postButton.setOnClickListener {
            postReview() // Panggil fungsi untuk mengirim review ke Firestore
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

    private fun postReview() {
        // Ambil nilai rating dan komentar dari UI
        val ratingBar = requireView().findViewById<RatingBar>(R.id.ratingBar)
        val comment = requireView().findViewById<EditText>(R.id.comment)

        val rating = ratingBar.rating
        val desc = comment.text.toString().trim()


        // Mendapatkan tanggal lokal saat ini
        val dateLokal = LocalDate.now()

        // Format tanggal ke "h/bln/thn"
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateFormatted: String = dateLokal.format(formatter) // Format sebagai String

        // Validasi input user
        if (rating == 0f || desc.isEmpty()) {
            Toast.makeText(requireContext(), "Rating dan komentar tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            return
        }

        // Buat objek review
        val review = hashMapOf(
            "rating" to rating,
            "desc" to desc,
            "date" to dateFormatted // Pastikan tipe data String

        )

        // Kirim data ke Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("foods")
            .document(foodId)
            .collection("reviews")
            .add(review)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Review berhasil diposting!", Toast.LENGTH_SHORT).show()
                // Reset input setelah berhasil
                ratingBar.rating = 0f
                comment.text.clear()
                fetchData() // Refresh data review setelah posting
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal memposting review: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun fetchData() {
        FirebaseFirestore.getInstance()
            .collection("foods")
            .document(foodId)
            .collection("reviews")
            .get()
            .addOnSuccessListener { result ->
                val newReviewList = result.map { document ->
                    document.toObject(ReviewData::class.java).copy(id = document.id)
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

