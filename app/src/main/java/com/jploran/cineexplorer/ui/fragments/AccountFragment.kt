package com.jploran.cineexplorer.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jploran.cineexplorer.databinding.FragmentAccountBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.jploran.cineexplorer.R
import com.jploran.cineexplorer.data.remote.model.UserDto

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val imageFileName = "profile_image.png"
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var fetchedUser: UserDto
    private var db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = auth.currentUser!!
        loadUserFromFireStore()

        val imageFile = File(requireContext().filesDir, imageFileName)
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
                // Permission denied, show a message to the user
            }
        }

        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                binding.userImageView.setImageBitmap(imageBitmap)
                saveBitmapToInternalStorage(requireContext(), imageBitmap, imageFileName)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            cameraLauncher.launch(takePictureIntent)
        }
    }

    private fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, filename: String): String? {
        val file = File(context.filesDir, filename)
        return try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
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
                    fetchedUser?.let { user ->
                        binding.nameTV.text = "${user.name} ${user.lastName}"
                        binding.birthdayTV.text = user.birthday
                        binding.emailTV.text = user.email
                    }
                    Log.d("Firestore", "Fetched user: $fetchedUser")
                } else {
                    Log.d("Firestore", "No user found with the given userId")
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