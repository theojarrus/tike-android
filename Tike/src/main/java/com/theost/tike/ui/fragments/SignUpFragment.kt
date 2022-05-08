package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.data.models.state.ExistStatus.Exist
import com.theost.tike.data.models.state.ExistStatus.NotFound
import com.theost.tike.data.models.state.Status.Error
import com.theost.tike.databinding.FragmentSignUpBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.adapters.delegates.LifestyleAdapterDelegate
import com.theost.tike.ui.extensions.changeText
import com.theost.tike.ui.viewmodels.SignUpViewModel
import com.theost.tike.ui.widgets.StateFragment

class SignUpFragment : StateFragment(R.layout.fragment_sign_up) {

    private val adapter: BaseAdapter = BaseAdapter()
    private val viewModel: SignUpViewModel by viewModels()
    private val binding: FragmentSignUpBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleEndlessStatus(it) }
        viewModel.nameStatus.observe(viewLifecycleOwner) { if (it is Error) showNameError() }
        viewModel.nickStatus.observe(viewLifecycleOwner) { if (it is Error) showNickError() }
        viewModel.userName.observe(viewLifecycleOwner) { binding.nameInput.changeText(it) }
        viewModel.userNick.observe(viewLifecycleOwner) { binding.nickInput.changeText(it) }

        viewModel.nickExistStatus.observe(viewLifecycleOwner) {
            when (it) {
                Exist -> binding.nickLayout.error = getString(R.string.error_nick_exist)
                NotFound -> binding.nickLayout.error = null
            }
        }

        viewModel.lifestyles.observe(viewLifecycleOwner) { lifestyles ->
            binding.lifestylesText.isGone = lifestyles.isEmpty()
            adapter.submitList(lifestyles)
        }

        binding.nameInput.addTextChangedListener { if (binding.nameInput.hasFocus()) hideErrors() }
        binding.nickInput.addTextChangedListener { if (binding.nickInput.hasFocus()) hideErrors() }

        binding.lifestylesList.adapter = adapter.apply {
            addDelegate(LifestyleAdapterDelegate { id -> viewModel.selectLifestyle(id) })
        }

        binding.signUpButton.setOnClickListener {
            viewModel.signUp(
                binding.nameInput.text.toString().trim(),
                binding.nickInput.text.toString().trim()
            )
        }

        viewModel.init()
    }

    override fun bindState(): StateViews = StateViews(
        rootView = binding.root,
        actionView = binding.signUpButton,
        loadingView = binding.loadingBar,
        errorMessage = getString(R.string.error_auth),
        disabledAdapter = adapter,
        disabledViews = listOf(binding.nameInput, binding.nickInput),
    )

    private fun showNameError() {
        binding.nameLayout.error = getString(R.string.error_name_format)
    }

    private fun showNickError() {
        binding.nickLayout.error = getString(R.string.error_nick_format)
    }

    private fun hideErrors() {
        binding.nameLayout.error = null
        binding.nickLayout.error = null
    }

    companion object {

        fun newInstance(): Fragment {
            return SignUpFragment()
        }
    }
}