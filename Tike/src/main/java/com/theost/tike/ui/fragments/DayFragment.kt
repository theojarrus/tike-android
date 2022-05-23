package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.data.models.state.EventAction.Reject
import com.theost.tike.data.models.state.EventAction.Info
import com.theost.tike.data.models.state.EventMode
import com.theost.tike.data.models.state.EventMode.SCHEDULE_PROPER
import com.theost.tike.data.models.state.EventMode.SCHEDULE_REFERENCE
import com.theost.tike.databinding.FragmentDayBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.adapters.delegates.EventAdapterDelegate
import com.theost.tike.ui.extensions.loadWithFadeIn
import com.theost.tike.ui.fragments.ScheduleFragmentDirections.Companion.actionScheduleFragmentToInfoFragment
import com.theost.tike.ui.utils.DisplayUtils.showConfirmationDialog
import com.theost.tike.ui.viewmodels.DayViewModel
import com.theost.tike.ui.widgets.StateFragment
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
                    is Info -> showEventInfo(action.id)
                    is Reject -> showConfirmationDialog(
                        requireContext(),
                        R.string.ask_event_delete
                    ) { deleteEvent(action.id, action.creator, action.mode) }
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
        loadingView = binding.loadingBar,
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

    private fun showEventInfo(id: String) {
        findNavController().navigate(actionScheduleFragmentToInfoFragment(id))
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
