package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.theost.tike.databinding.FragmentAddingBinding
import com.theost.tike.ui.adapters.core.AddingAdapter

class AddingFragment : Fragment() {

    private var _binding: FragmentAddingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddingBinding.inflate(layoutInflater, container, false)

        binding.addingPager.adapter = AddingAdapter(childFragmentManager, lifecycle)
        binding.addingPager.isUserInputEnabled = false

        binding.creationSwitchButton.setOnClickListener { view ->
            switchAddingButtons(view.id)
            switchFragment(AddingAdapter.ADDING_CREATION_POSITION)
        }

        binding.joiningSwitchButton.setOnClickListener { view ->
            switchAddingButtons(view.id)
            switchFragment(AddingAdapter.ADDING_JOINING_POSITION)
        }

        return binding.root
    }

    private fun switchAddingButtons(id: Int) {
        binding.creationSwitchButton.isEnabled = binding.creationSwitchButton.id != id
        binding.joiningSwitchButton.isEnabled = binding.joiningSwitchButton.id != id
    }

    private fun switchFragment(position: Int) {
        binding.addingPager.currentItem = position
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance(): Fragment {
            return AddingFragment()
        }
    }

}