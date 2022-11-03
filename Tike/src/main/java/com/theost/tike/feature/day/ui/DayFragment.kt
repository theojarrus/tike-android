package com.theost.tike.feature.day.ui

import android.os.Bundle
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.extension.getSerializableExtra
import com.theost.tike.common.extension.loadWithFadeIn
import com.theost.tike.common.extension.navigate
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.common.recycler.element.event.EventAdapterDelegate
import com.theost.tike.common.util.DisplayUtils.showConfirmationDialog
import com.theost.tike.core.model.StateStatus.Initial
import com.theost.tike.core.model.StateViews
import com.theost.tike.core.ui.BaseStateFragment
import com.theost.tike.databinding.FragmentDayBinding
import com.theost.tike.domain.model.multi.EventAction.Info
import com.theost.tike.feature.day.presentation.DayState
import com.theost.tike.feature.day.presentation.DayViewModel
import com.theost.tike.feature.schedule.ui.ScheduleFragmentDirections.Companion.actionScheduleToInfo
import org.threeten.bp.LocalDate

class DayFragment : BaseStateFragment<DayState, DayViewModel>(R.layout.fragment_day) {

    private val binding: FragmentDayBinding by viewBinding()
    private val adapter: BaseAdapter = BaseAdapter()

    override val viewModel: DayViewModel by viewModels()

    override val isHandlingState: Boolean = true
    override val isRefreshingErrorOnly: Boolean = true

    override fun setupView() = with(binding) {
        recyclerView.adapter = adapter.apply {
            addDelegate(EventAdapterDelegate { action ->
                when (action) {
                    is Info -> navigate(actionScheduleToInfo(action.id, action.creator))
                    else -> showConfirmationDialog(
                        requireContext(),
                        R.string.ask_event_delete
                    ) { viewModel.dispatchEventAction(action) }
                }
            })
        }
    }

    override fun render(state: DayState) = with(binding) {
        val isEmptyGone = !state.status.isLoaded() || state.items.isNotEmpty()
        if (!isEmptyGone) emptyView.emptyImage.loadWithFadeIn(R.drawable.empty_events)
        emptyView.root.isGone = isEmptyGone
        adapter.submitList(state.items)
    }

    override val stateViews: StateViews
        get() = StateViews(
            swipeRefresh = binding.swipeRefresh,
            loadingView = binding.loadingView,
            errorView = binding.errorView
        )

    override val initialState: DayState
        get() = DayState(
            status = Initial,
            items = emptyList()
        )

    override val initialAction: DayViewModel.() -> Unit = {
        observeEvents(arguments?.getSerializableExtra(ARG_DATE, LocalDate::class.java))
    }

    companion object {

        private const val ARG_DATE = "date"

        fun newInstance(day: LocalDate?): Fragment {
            return DayFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DATE, day)
                }
            }
        }
    }
}
