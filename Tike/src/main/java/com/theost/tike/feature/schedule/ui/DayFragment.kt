package com.theost.tike.feature.schedule.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.extension.loadWithFadeIn
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.common.recycler.element.event.EventAdapterDelegate
import com.theost.tike.common.util.DisplayUtils.showConfirmationDialog
import com.theost.tike.core.deprecated.StateFragment
import com.theost.tike.databinding.FragmentDayBinding
import com.theost.tike.domain.model.multi.EventAction.Info
import com.theost.tike.domain.model.multi.EventAction.Reject
import com.theost.tike.domain.model.multi.EventMode
import com.theost.tike.domain.model.multi.EventMode.SCHEDULE_PROPER
import com.theost.tike.domain.model.multi.EventMode.SCHEDULE_REFERENCE
import com.theost.tike.feature.schedule.presentation.DayViewModel
import com.theost.tike.feature.schedule.ui.ScheduleFragmentDirections.Companion.actionScheduleFragmentToInfoFragment
import org.threeten.bp.LocalDate

class DayFragment : StateFragment(R.layout.fragment_day) {

    private val adapter: BaseAdapter = BaseAdapter()

    private val viewModel: DayViewModel by viewModels()
    private val binding: FragmentDayBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadingStatus.observe(viewLifecycleOwner) { handleStatus(it) }

        viewModel.events.observe(viewLifecycleOwner) { events ->
            displayEmptyView(events.isEmpty())
            adapter.submitList(events)
        }

        binding.eventsList.adapter = adapter.apply {
            addDelegate(EventAdapterDelegate { action ->
                when (action) {
                    is Reject -> showConfirmationDialog(
                        requireContext(),
                        R.string.ask_event_delete
                    ) { deleteEvent(action.id, action.creator, action.mode) }
                    is Info -> findNavController().navigate(
                        actionScheduleFragmentToInfoFragment(
                            action.id,
                            action.creator
                        )
                    )
                    else -> {}
                }
            })
        }

        arguments?.let { arguments ->
            viewModel.init(
                LocalDate.ofYearDay(
                    arguments.getInt(ARG_DATE_YEAR),
                    arguments.getInt(ARG_DATE_DAY)
                )
            )
        } ?: displayEmptyView(true)
    }

    override fun bindState(): StateViews = StateViews(
        loadingView = binding.loadingView,
        errorMessage = getString(R.string.error_network)
    )

    private fun deleteEvent(id: String, creator: String, mode: EventMode) {
        when (mode) {
            SCHEDULE_PROPER -> viewModel.deleteProperEvent(id)
            SCHEDULE_REFERENCE -> viewModel.deleteReferenceEvent(id, creator)
            else -> {}
        }
    }

    private fun displayEmptyView(isVisible: Boolean) {
        with(binding.emptyDayView) {
            binding.emptyDayView.root.isVisible = isVisible
            if (isVisible) emptyImage.loadWithFadeIn(R.drawable.empty_events)
        }
    }

    companion object {

        private const val ARG_DATE_YEAR = "date_year"
        private const val ARG_DATE_DAY = "date_day"

        fun newInstance(day: LocalDate): Fragment {
            return DayFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_DATE_YEAR, day.year)
                    putInt(ARG_DATE_DAY, day.dayOfYear)
                }
            }
        }
    }
}
