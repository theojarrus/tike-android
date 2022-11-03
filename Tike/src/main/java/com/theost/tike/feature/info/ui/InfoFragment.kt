package com.theost.tike.feature.info.ui

import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.extension.navigate
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.common.recycler.element.option.OptionAdapterDelegate
import com.theost.tike.common.recycler.element.title.TitleAdapterDelegate
import com.theost.tike.common.recycler.element.user.UserAdapterDelegate
import com.theost.tike.core.model.StateStatus.Initial
import com.theost.tike.core.model.StateViews
import com.theost.tike.core.ui.BaseStateBottomFragment
import com.theost.tike.databinding.FragmentInfoBinding
import com.theost.tike.domain.model.multi.OptionAction.LocationOptionAction
import com.theost.tike.feature.info.presentation.InfoState
import com.theost.tike.feature.info.presentation.InfoViewModel
import com.theost.tike.feature.info.ui.InfoFragmentDirections.Companion.actionInfoToLocation
import com.theost.tike.feature.info.ui.InfoFragmentDirections.Companion.actionInfoToProfile

class InfoFragment : BaseStateBottomFragment<InfoState, InfoViewModel>(R.layout.fragment_info) {

    private val args: InfoFragmentArgs by navArgs()
    private val binding: FragmentInfoBinding by viewBinding()
    private val adapter: BaseAdapter = BaseAdapter()

    override val viewModel: InfoViewModel by viewModels()

    override val isHandlingState: Boolean = true
    override val isRefreshingErrorOnly: Boolean = true

    override fun setupView() = with(binding) {
        recycler.adapter = adapter.apply {
            addDelegate(TitleAdapterDelegate())
            addDelegate(UserAdapterDelegate { navigate(actionInfoToProfile(it)) })
            addDelegate(OptionAdapterDelegate { action ->
                if (action is LocationOptionAction) navigate(actionInfoToLocation(action.location))
            })
        }
    }

    override fun render(state: InfoState) = with(binding) {
        binding.emptyView.isGone = !state.status.isLoaded() || state.items.isNotEmpty()
        adapter.submitList(state.items)
    }

    override val stateViews: StateViews
        get() = StateViews(
            loadingView = binding.loadingView,
            errorView = binding.errorView
        )

    override val initialState: InfoState
        get() = InfoState(
            status = Initial,
            items = emptyList()
        )

    override val initialAction: InfoViewModel.() -> Unit = {
        fetchEventInfo(args.id, args.creator)
    }
}
