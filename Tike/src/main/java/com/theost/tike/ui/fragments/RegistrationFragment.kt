package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.theost.tike.R
import com.theost.tike.data.models.state.Status
import com.theost.tike.databinding.FragmentRegistrationBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.adapters.delegates.LifestyleAdapterDelegate
import com.theost.tike.ui.viewmodels.RegistrationViewModel
import com.theost.tike.utils.StringUtils

class RegistrationFragment : Fragment() {

    private val adapter: BaseAdapter = BaseAdapter()

    private val viewModel: RegistrationViewModel by viewModels()

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)

        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.nameInput.setText(name)
            binding.nameInput.setSelection(name.length)
        }

        viewModel.userNick.observe(viewLifecycleOwner) { nick ->
            binding.nickInput.setText(nick)
            binding.nickInput.setSelection(nick.length)
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                Status.Loading -> showLoading()
                Status.Success -> hideLoading()
                Status.Error -> {
                    showErrorToast()
                    hideLoading()
                }
            }
        }

        viewModel.checkingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                Status.Success -> { /* do nothing */ }
                Status.Loading -> { /* do nothing */ }
                Status.Error -> binding.nickLayout.error = getString(R.string.error_nick_exist)
            }
        }

        binding.lifestylesList.adapter = adapter.apply {
            addDelegate(LifestyleAdapterDelegate { id -> viewModel.onLifestyleItemClicked(id) })
        }

        viewModel.lifestyles.observe(viewLifecycleOwner) { lifestyles ->
            adapter.submitList(lifestyles)
            binding.lifestylesText.isGone = lifestyles.isEmpty()
        }

        binding.nameInput.addTextChangedListener { binding.nameLayout.error = null }
        binding.nickInput.addTextChangedListener { binding.nickLayout.error = null }

        binding.registerUserButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val nick = binding.nickInput.text.toString()
            val isNameCorrect = StringUtils.isNameCorrect(name)
            val isNickCorrect = StringUtils.isNickCorrect(nick)

            if (!isNameCorrect) binding.nameLayout.error = getString(R.string.error_name_format)
            if (!isNickCorrect) binding.nickLayout.error = getString(R.string.error_nick_format)

            if (isNameCorrect && isNickCorrect) {
                viewModel.signUp(StringUtils.formatName(name), StringUtils.formatNick(nick))
            }
        }

        return binding.root
    }

    private fun showLoading() {
        binding.registerUserButton.visibility = View.INVISIBLE
        binding.loadingBar.isGone = false
    }

    private fun hideLoading() {
        binding.registerUserButton.visibility = View.VISIBLE
        binding.loadingBar.isGone = true
    }

    private fun showErrorToast() {
        Toast.makeText(context, R.string.error_auth, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}