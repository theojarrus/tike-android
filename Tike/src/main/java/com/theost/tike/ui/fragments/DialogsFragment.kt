package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.databinding.FragmentDialogsBinding

class DialogsFragment : Fragment(R.layout.fragment_dialogs) {

    private val binding: FragmentDialogsBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}