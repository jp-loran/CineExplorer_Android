package com.jploran.cineexplorer.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jploran.cineexplorer.R
import com.jploran.cineexplorer.databinding.FragmentLoginBinding
import com.jploran.cineexplorer.databinding.FragmentRegisterBinding
import java.util.Calendar


class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private var db = Firebase.firestore
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val loginButton = binding.loginTV
        val registerButton = binding.register

        binding.birthdayPicker.setOnClickListener {
            showDatePickerDialog()
        }

        registerButton.setOnClickListener {
            if (validateFields()) {
                val name = binding.etName.text.toString()
                val lastName = binding.etLastName.text.toString()
                val birthday = binding.birthdayPicker.text.toString()
                val email = binding.etRegisterEmail.text.toString()
                val password = binding.etRegisterPassword.text.toString()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val newUser = hashMapOf(
                                "name" to name,
                                "lastName" to lastName,
                                "birthday" to birthday,
                                "email" to email,
                                "userId" to task.result?.user?.uid
                            )

                            db.collection("User")
                                .add(newUser)
                                .addOnSuccessListener {
                                    Snackbar.make(binding.root, "Registration successful", Snackbar.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_registerFragment_to_successRegisterFragment)
                                }
                        } else {
                            Snackbar.make(binding.root, "Registration failed: ${task.exception?.message}", Snackbar.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        loginButton.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return binding.root
    }


    private fun validateFields(): Boolean {
        val name = binding.etName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val birthday = binding.birthdayPicker.text.toString().trim()
        val email = binding.etRegisterEmail.text.toString().trim()
        val password = binding.etRegisterPassword.text.toString().trim()

        if (name.isEmpty()) {
            Snackbar.make(binding.root, "Please enter your name", Snackbar.LENGTH_SHORT).show()
            return false
        }

        if (lastName.isEmpty()) {
            Snackbar.make(binding.root, "Please enter your last name", Snackbar.LENGTH_SHORT).show()
            return false
        }

        if (birthday.isEmpty()) {
            Snackbar.make(binding.root, "Please enter your birthday", Snackbar.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty()) {
            Snackbar.make(binding.root, "Please enter your email", Snackbar.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Snackbar.make(binding.root, "Please enter your password", Snackbar.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
            binding.birthdayPicker.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

}