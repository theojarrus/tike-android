package com.theost.tike.feature.dialogs.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.core.recycler.user.UserAdapterDelegate
import com.theost.tike.core.screen.StateFragment
import com.theost.tike.databinding.FragmentDialogsBinding
import com.theost.tike.feature.dialogs.presentation.DialogsViewModel

class DialogsFragment : StateFragment(R.layout.fragment_dialogs) {

    private val adapter: BaseAdapter = BaseAdapter()

    private val viewModel: DialogsViewModel by viewModels()
    private val binding: FragmentDialogsBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleStatus(it) }
        viewModel.users.observe(viewLifecycleOwner) { adapter.submitList(it) }

        binding.usersList.adapter = adapter.apply {
            addDelegate(UserAdapterDelegate {
                makeText(requireContext(), R.string.feature_not_ready, Toast.LENGTH_SHORT).show()
            })
        }
    }

    override fun bindState(): StateViews = StateViews(
        toolbar = binding.toolbar,
        loadingView = binding.loadingBar,
        errorView = binding.errorView,
        disabledAdapter = adapter
    )
}
