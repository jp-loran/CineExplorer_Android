package com.jploran.cineexplorer.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jploran.cineexplorer.R
import com.jploran.cineexplorer.data.remote.model.UserDto
import com.jploran.cineexplorer.databinding.FragmentAccountBinding
import java.io.ByteArrayOutputStream
import java.io.File


class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var fetchedUser: UserDto
    private var db = Firebase.firestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = auth.currentUser!!
        loadUserFromFireStore()

        val imageFile = File(requireContext().filesDir, "profile_image.png")
        if (imageFile.exists()) {
            val savedBitmap = loadBitmapFromInternalStorage(imageFile.absolutePath)
            binding.userImageView.setImageBitmap(savedBitmap)
        }

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            } else {
                // Handle permission denied case
            }
        }

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                binding.userImageView.setImageBitmap(imageBitmap)
                uploadImageToFirebase(imageBitmap)
            }
        }

        binding.userImageView.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
                openCamera()
            }
        }

        binding.logout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_accountFragment_to_loginFragment)
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            cameraLauncher.launch(takePictureIntent)
        }
    }

    private fun uploadImageToFirebase(bitmap: Bitmap) {

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        val profileImageRef = storageReference.child("profile_images/${currentUser.uid}.png")

        val uploadTask = profileImageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            Log.d("FirebaseStorage", "Image uploaded successfully")

            profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                db.collection("User")
                    .whereEqualTo("userId", currentUser.uid)
                Log.d("FirebaseStorage", "Download URL: $downloadUrl")

                saveUserProfileImageUrl(downloadUrl)
            }
        }.addOnFailureListener { exception ->
            Log.e("FirebaseStorage", "Error uploading image", exception)
        }
    }

    private fun saveUserProfileImageUrl(url: String) {
        db.collection("User")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents[0]
                    val docId = document.id

                    db.collection("User").document(docId)
                        .update("imageUrl", url)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Profile image URL updated")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firestore", "Error updating profile image URL", exception)
                        }
                } else {
                    val newUser = mapOf(
                        "userId" to currentUser.uid,
                        "imageUrl" to url
                    )

                    db.collection("User").add(newUser)
                        .addOnSuccessListener {
                            Log.d("Firestore", "New user document created with profile image URL")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firestore", "Error creating new user document", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error querying user document", exception)
            }
    }

    private fun loadBitmapFromInternalStorage(filepath: String): Bitmap? {
        return BitmapFactory.decodeFile(filepath)
    }

    private fun loadUserFromFireStore() {
        db.collection("User")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents[0]
                    fetchedUser = document.toObject(UserDto::class.java)!!
                    // Update UI with fetchedUser data
                    binding.nameTV.text = "${fetchedUser.name} ${fetchedUser.lastName}"
                    binding.birthdayTV.text = fetchedUser.birthday
                    binding.emailTV.text = fetchedUser.email
                    Log.d("Firestore", "Fetched user: ${fetchedUser.imageUrl}")

                    if (!fetchedUser.imageUrl.isNullOrEmpty()) {
                        Glide.with(this).load(fetchedUser.imageUrl).into(binding.userImageView)
                    } else {
                        binding.userImageView.setImageResource(R.drawable.default_user)
                    }

                    Log.d("Firestore", "Fetched user: $fetchedUser")
                } else {
                    Log.d("Firestore", "No user found with the given userId: ${currentUser.uid}")
                    binding.userImageView.setImageResource(R.drawable.default_user)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }


    companion object {
        @JvmStatic
        fun newInstance() = AccountFragment()
    }
}
