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
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class Detail : Fragment() {

    private var foodId: String = ""
    private var title: String? = null
    private var author: String? = null
    private var imageFood: String? = null
    private var ingredients: ArrayList<String>? = null
    private var tutorial: ArrayList<String>? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewAdapter: ReviewAdapter
    private val reviewList = mutableListOf<ReviewData>() // List untuk data review

    private lateinit var ratingBar: RatingBar
    private lateinit var comment: EditText
    private lateinit var postButton: Button

    private lateinit var nameText: TextView
    private lateinit var profileImage: ShapeableImageView

    private var name: String? = null
    private var profileUrl: String? = null

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
        val tutorial = arguments?.getStringArrayList("tutorial")


        // Siapkan data untuk dikirim

        // Tampilkan data ke dalam view
        view.findViewById<TextView>(R.id.title).text = title
        view.findViewById<TextView>(R.id.author).text = author
        view.findViewById<TextView>(R.id.ingredients).text =
            ingredients?.mapIndexed { index, item -> "${index + 1}. $item" }?.joinToString("\n") ?: ""
        view.findViewById<TextView>(R.id.tutorial).text =
            tutorial?.mapIndexed { index, item -> "${index + 1}. $item" }?.joinToString("\n") ?: ""

        checkBookmarkStatus(foodId)

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

        // Inisialisasi nameText dan profileImage
        nameText = view.findViewById(R.id.name)
        profileImage = view.findViewById(R.id.profile)

        // Fetch data review dari Firestore
        fetchData()

        // Ambil data pengguna untuk nama dan foto profil
        fetchUserData()

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

        val name = name
        val profile = profileUrl
        val userId = FirebaseAuth.getInstance().currentUser?.uid  // ID pengguna yang sedang login

        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        // Mendapatkan tanggal lokal saat ini
        val dateLokal = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateFormatted: String = dateLokal.format(formatter) // Format sebagai String

        // Validasi input user
        if (rating == 0f || desc.isEmpty()) {
            Toast.makeText(requireContext(), "Rating dan komentar tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            return
        }

        // Cek apakah user sudah memberi review untuk foodId ini
        val db = FirebaseFirestore.getInstance()
        db.collection("foods")
            .document(foodId)
            .collection("reviews")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    Toast.makeText(requireContext(), "Anda sudah memberikan review untuk makanan ini!", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Jika belum ada review, buat objek review baru
                val review = hashMapOf(
                    "rating" to rating,
                    "desc" to desc,
                    "date" to dateFormatted, // Pastikan tipe data String
                    "name" to name,
                    "profile" to profile,
                    "userId" to userId // Simpan ID user untuk referensi di masa mendatang
                )

                // Kirim data ke Firestore
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
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal memeriksa ulasan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchUserData() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid  // Ambil ID user yang sedang login

        if (userId != null) {
            db.collection("account").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        name = document.getString("name")
                        profileUrl = document.getString("profile")

                        // Update nama pengguna
                        nameText.text = name ?: "User"  // Set default text if name is null

                        // Cek apakah field "profile" berisi URL langsung
                        if (profileUrl.isNullOrEmpty()) {
                            profileImage.setImageResource(R.drawable.pfp)  // Set default image if no profile URL
                        } else {
                            // Jika URL langsung ke gambar, load menggunakan Glide
                            Glide.with(requireContext())
                                .load(profileUrl)
                                .placeholder(R.drawable.pfp)  // Placeholder selama gambar dimuat
                                .error(R.drawable.pfp)  // Gambar default jika terjadi error
                                .into(profileImage)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Error getting user data", exception)
                }
        }
    }

    private fun saveToBookmark(foodId: String) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "" // Ambil UID pengguna yang sedang login

        // Ambil data resep lengkap berdasarkan foodId
        db.collection("foods")
            .document(foodId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Ambil data dari dokumen asli
                    val title = document.getString("title")
                    val author = document.getString("author")
                    val imageFood = document.getString("imageFood")
                    val ingredients = document.get("ingredients") as? ArrayList<String>
                    val tutorial = document.get("tutorial") as? ArrayList<String>

                    // Siapkan data untuk disimpan ke bookmark
                    val bookmarkData = hashMapOf(
                        "foodId" to foodId,
                        "title" to title,
                        "author" to author,
                        "imageFood" to imageFood,
                        "ingredients" to ingredients,
                        "tutorial" to tutorial
                    )

                    // Simpan data ke koleksi bookmarkedRecipes
                    db.collection("users")
                        .document(userId)
                        .collection("bookmarkedRecipes")
                        .document(foodId)
                        .set(bookmarkData)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Resep berhasil disimpan ke bookmark!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Gagal menyimpan resep: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Resep tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal mengambil data resep: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun checkBookmarkStatus(foodId: String) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        db.collection("users")
            .document(userId)
            .collection("bookmarkedRecipes")
            .document(foodId)
            .get()
            .addOnSuccessListener { document ->
                val bookmarkButton = requireView().findViewById<ImageView>(R.id.bookmarkButton)
                if (document.exists()) {
                    bookmarkButton.setImageResource(R.drawable.baseline_bookmark_24) // Bookmark aktif
                    bookmarkButton.setOnClickListener {
                        removeFromBookmark(foodId) // Hapus bookmark jika sudah ada
                    }
                } else {
                    bookmarkButton.setImageResource(R.drawable.baseline_bookmark_border_24) // Bookmark tidak aktif
                    bookmarkButton.setOnClickListener {
                        saveToBookmark(foodId) // Tambah bookmark jika belum ada
                    }
                }
            }
            .addOnFailureListener {
                Log.e("BookmarkStatus", "Gagal mengecek status bookmark")
            }
    }


    private fun removeFromBookmark(foodId: String) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "" // Ambil UID pengguna yang sedang login

        db.collection("users")
            .document(userId)
            .collection("bookmarkedRecipes")
            .document(foodId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Resep berhasil dihapus dari bookmark!", Toast.LENGTH_SHORT).show()
                val bookmarkButton = requireView().findViewById<ImageView>(R.id.bookmarkButton)
                bookmarkButton.setImageResource(R.drawable.baseline_bookmark_border_24) // Bookmark tidak aktif
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal menghapus bookmark: ${e.message}", Toast.LENGTH_SHORT).show()
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