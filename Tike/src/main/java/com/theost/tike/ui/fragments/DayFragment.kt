package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.data.models.state.ActionMode.CANCEL
import com.theost.tike.databinding.FragmentDayBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.adapters.delegates.EventAdapterDelegate
import com.theost.tike.ui.extensions.load
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
            addDelegate(EventAdapterDelegate() { id, mode ->
                if (mode == CANCEL) showConfirmationDialog(
                    requireContext(),
                    R.string.ask_event_delete
                ) {  viewModel.deleteEvent(id) }
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

    private fun displayEmptyView(isVisible: Boolean) {
        with(binding.emptyDayView) {
            binding.emptyDayView.root.isVisible = isVisible
            if (isVisible) emptyImage.apply {
                alpha = 0f
                load(R.drawable.events_empty_banner)
                animate().withStartAction { alpha = 0f }.alpha(1f).duration = 500
            }
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