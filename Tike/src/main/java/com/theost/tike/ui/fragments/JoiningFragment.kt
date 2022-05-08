package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.databinding.FragmentJoiningBinding

class JoiningFragment : Fragment(R.layout.fragment_joining) {

    private val binding: FragmentJoiningBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

        fun newInstance(): Fragment {
            return JoiningFragment()
        }
    }
}