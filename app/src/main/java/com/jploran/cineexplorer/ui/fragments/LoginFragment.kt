package com.jploran.cineexplorer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jploran.cineexplorer.R
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jploran.cineexplorer.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val registerButton = binding.registerTV
        val loginButton = binding.login

        loginButton.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            val validationOk = validateLoginFields(email, password)
            if(validationOk){
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            findNavController().navigate(R.id.action_loginFragment_to_moviesFragment)
                        }else{
                            Snackbar.make(binding.root, task.exception.toString(), 500).show()
                        }
                    }
            }
        }

        registerButton.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun validateLoginFields(email: String, password: String): Boolean {
        var validation = true
        if(password.isEmpty()){
            Snackbar.make(binding.root, "Enter a password", 500).show()
            validation = false
        }
        if(email.isEmpty()){
         Snackbar.make(binding.root, "Enter an email", 500).show()
            validation = false
        }
        return validation
    }

}