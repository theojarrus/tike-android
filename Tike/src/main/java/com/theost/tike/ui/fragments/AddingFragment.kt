package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.data.models.state.AddingMode.CREATING
import com.theost.tike.data.models.state.AddingMode.JOINING
import com.theost.tike.databinding.FragmentAddingBinding
import com.theost.tike.ui.adapters.pages.AddingPageAdapter
import com.theost.tike.ui.viewmodels.AddingViewModel

class AddingFragment : Fragment(R.layout.fragment_adding) {

    private val viewModel: AddingViewModel by viewModels()
    private val binding: FragmentAddingBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.addingPager) {
            adapter = AddingPageAdapter(childFragmentManager, lifecycle)
            isUserInputEnabled = false
        }

        binding.creationSwitchButton.setOnClickListener { viewModel.setPosition(CREATING.position) }
        binding.joiningSwitchButton.setOnClickListener { viewModel.setPosition(JOINING.position) }

        viewModel.position.observe(viewLifecycleOwner) { position ->
            binding.addingPager.currentItem = position
            when (position) {
                0 -> switchAddingButtons(R.id.creationSwitchButton)
                1 -> switchAddingButtons(R.id.joiningSwitchButton)
            }
        }
    }

    private fun switchAddingButtons(checkedId: Int) {
        binding.creationSwitchButton.isEnabled = binding.creationSwitchButton.id != checkedId
        binding.joiningSwitchButton.isEnabled = binding.joiningSwitchButton.id != checkedId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.addingPager.adapter = null
    }
}
