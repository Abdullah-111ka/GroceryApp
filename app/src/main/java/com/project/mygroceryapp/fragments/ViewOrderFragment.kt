package com.project.mygroceryapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.mygroceryapp.databinding.FragmentViewOrdersBinding

class ViewOrderFragment : Fragment() {

    private lateinit var binding : FragmentViewOrdersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }


}