package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.theost.tike.R
import com.theost.tike.data.models.state.Status
import com.theost.tike.ui.viewmodels.DayViewModel
import com.theost.tike.databinding.FragmentDayBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.adapters.delegates.EventAdapterDelegate
import org.threeten.bp.LocalDate

class DayFragment : Fragment() {

    private val adapter: BaseAdapter = BaseAdapter()

    private val viewModel: DayViewModel by viewModels()

    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayBinding.inflate(layoutInflater, container, false)

        viewModel.allData.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
            binding.emptyDayView.root.isVisible = events.isEmpty()
            if (binding.emptyDayView.root.isVisible) animateEmptyView()
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                Status.Error -> showErrorToast()
                Status.Loading -> { /* do nothing */ }
                Status.Success -> { /* do nothing */ }
            }
        }

        binding.eventsList.adapter = adapter.apply {
            addDelegate(EventAdapterDelegate())
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { arguments ->
            viewModel.loadEvents(
                LocalDate.ofYearDay(
                    arguments.getInt(ARG_DATE_YEAR),
                    arguments.getInt(ARG_DATE_DAY)
                )
            )
        }
    }

    private fun animateEmptyView() {
        binding.emptyDayView.root.alpha = 0f
        binding.emptyDayView.root.animate().alpha(1.0f).duration = 500
    }

    private fun showErrorToast() {
        Toast.makeText(context, R.string.error_network, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val ARG_DATE_YEAR = "date_year"
        private const val ARG_DATE_DAY = "date_day"

        fun newInstance(day: LocalDate): Fragment {
            val fragment = DayFragment()
            val args = Bundle()
            args.putInt(ARG_DATE_YEAR, day.year)
            args.putInt(ARG_DATE_DAY, day.dayOfYear)
            fragment.arguments = args
            return fragment
        }
    }

}