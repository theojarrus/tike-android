package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theost.tike.data.models.state.OptionAction.LocationOptionAction
import com.theost.tike.data.models.state.Status.*
import com.theost.tike.databinding.FragmentInfoBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.adapters.delegates.OptionAdapterDelegate
import com.theost.tike.ui.adapters.delegates.TitleAdapterDelegate
import com.theost.tike.ui.adapters.delegates.UserAdapterDelegate
import com.theost.tike.ui.fragments.InfoFragmentDirections.Companion.actionInfoFragmentToLocationFragment
import com.theost.tike.ui.fragments.InfoFragmentDirections.Companion.actionInfoFragmentToProfileFragment
import com.theost.tike.ui.viewmodels.InfoViewModel

class InfoFragment : BottomSheetDialogFragment() {

    private val adapter: BaseAdapter = BaseAdapter()

    private val viewModel: InfoViewModel by viewModels()
    private val args: InfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentInfoBinding.inflate(layoutInflater)

        viewModel.items.observe(viewLifecycleOwner) { users ->
            binding.emptyView.isGone = users.isNotEmpty()
            adapter.submitList(users)
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                Loading -> binding.loadingBar.isGone = false
                Success -> binding.loadingBar.isGone = true
                Error -> {
                    binding.loadingBar.isGone = true
                    binding.errorView.isGone = false
                }
            }
        }

        binding.usersList.adapter = adapter.apply {
            addDelegate(TitleAdapterDelegate())
            addDelegate(OptionAdapterDelegate() { action ->
                if (action is LocationOptionAction) {
                    findNavController().navigate(actionInfoFragmentToLocationFragment(action.location))
                }
            })
            addDelegate(UserAdapterDelegate { uid ->
                findNavController().navigate(actionInfoFragmentToProfileFragment(uid))
            })
        }

        viewModel.init(args.id, args.creator)

        return binding.root
    }
}
