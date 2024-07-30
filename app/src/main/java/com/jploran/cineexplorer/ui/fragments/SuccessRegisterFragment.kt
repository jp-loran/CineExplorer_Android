package com.jploran.cineexplorer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.jploran.cineexplorer.R
import com.jploran.cineexplorer.databinding.FragmentRegisterBinding
import com.jploran.cineexplorer.databinding.FragmentSuccessRegisterBinding

class SuccessRegisterFragment : Fragment() {

    private var _binding: FragmentSuccessRegisterBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSuccessRegisterBinding.inflate(inflater, container, false)

        val explore = binding.explore
        explore.setOnClickListener {
            findNavController().navigate(R.id.action_successRegisterFragment_to_moviesFragment)
        }

        return binding.root
    }

}