package com.theost.tike.feature.adding.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.common.recycler.delegate.DelegateItem
import com.theost.tike.common.recycler.element.card.CardAdapterDelegate
import com.theost.tike.common.util.DateUtils
import com.theost.tike.common.util.StringUtils.switchIfEmpty
import com.theost.tike.core.deprecated.StateFragment
import com.theost.tike.databinding.FragmentCreationBinding
import com.theost.tike.domain.model.multi.RepeatMode
import com.theost.tike.domain.model.multi.Status.Success
import com.theost.tike.feature.adding.presentation.CreationViewModel
import com.theost.tike.feature.adding.presentation.EventViewModel
import com.theost.tike.feature.creation.ui.AddingFragmentDirections.Companion.actionAddingFragmentToLocationFragment
import com.theost.tike.feature.creation.ui.AddingFragmentDirections.Companion.actionAddingFragmentToParticipantsFragment
import com.theost.tike.feature.schedule.presentation.CalendarViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.lang.String.format

class CreationFragment : StateFragment(R.layout.fragment_creation) {

    private var eventDate = LocalDate.now()
    private var eventBeginTime = LocalTime.now()
    private var eventEndTime = LocalTime.now()

    private val adapter: BaseAdapter = BaseAdapter()

    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private val eventViewModel: EventViewModel by activityViewModels()
    private val viewModel: CreationViewModel by viewModels()
    private val binding: FragmentCreationBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.openParticipantsButton.setOnClickListener { openParticipantsAdding() }
        binding.locationInput.setOnClickListener { openLocationPicker() }
        binding.dateDayInput.setOnClickListener { showDatePicker() }
        binding.dateBeginInput.setOnClickListener { showBeginTimePicker() }
        binding.dateEndInput.setOnClickListener { showEndTimePicker() }
        binding.createEventButton.setOnClickListener { addEvent() }
        binding.numberPlusButton.setOnClickListener { viewModel.updateParticipantsLimit(1) }
        binding.numberMinusButton.setOnClickListener { viewModel.updateParticipantsLimit(-1) }
        binding.clearLocation.setOnClickListener { eventViewModel.setLocation(null) }

        binding.participantsList.adapter = adapter.apply {
            addDelegate(CardAdapterDelegate { id -> viewModel.removeParticipant(id) })
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                Success -> {
                    calendarViewModel.setDate(eventDate)
                    eventViewModel.reset()
                    findNavController().navigateUp()
                }
                else -> handleStatus(status)
            }
        }

        viewModel.participantsLimit.observe(viewLifecycleOwner) { limit ->
            binding.numberCounter.text = format(getString(R.string.vacancy_count), limit)
        }

        viewModel.participants.observe(viewLifecycleOwner) { participants ->
            eventViewModel.setMembers(participants.map { it.uid })
            adapter.submitList(mutableListOf<DelegateItem>().apply { addAll(participants) })
        }

        viewModel.eventDate.observe(viewLifecycleOwner) { date ->
            eventDate = date
            binding.dateDayInput.setText(
                DateUtils.formatDate(
                    date.year,
                    date.monthValue,
                    date.dayOfMonth
                )
            )
        }

        viewModel.eventBeginTime.observe(viewLifecycleOwner) { beginTime ->
            eventBeginTime = beginTime
            binding.dateBeginInput.setText(
                DateUtils.formatTime(
                    beginTime.hour,
                    beginTime.minute
                )
            )
        }

        viewModel.eventEndTime.observe(viewLifecycleOwner) { endTime ->
            eventEndTime = endTime
            binding.dateEndInput.setText(
                DateUtils.formatTime(
                    endTime.hour,
                    endTime.minute
                )
            )
        }

        eventViewModel.location.observe(viewLifecycleOwner) { location ->
            viewModel.updateLocation(location)
            binding.locationInput.setText(location?.address ?: "")
            binding.clearLocation.isGone = location == null
        }

        viewModel.init(eventViewModel.members.value ?: emptyList())
    }

    override fun bindState(): StateViews = StateViews(
        rootView = binding.root,
        actionView = binding.createEventButton,
        loadingView = binding.loadingView,
        errorMessage = getString(R.string.error_network),
        disabledAdapter = adapter,
        disabledViews = listOf(
            binding.titleInput,
            binding.descriptionInput,
            binding.dateDayInput,
            binding.dateBeginInput,
            binding.dateEndInput,
            binding.repeatButtonsGroup
        )
    )

    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day -> viewModel.updateEventDate(year, month + 1, day) },
            eventDate.year,
            eventDate.monthValue - 1,
            eventDate.dayOfMonth
        ).show()
    }

    private fun showBeginTimePicker() {
        TimePickerDialog(
            requireContext(),
            { _, hour, minute -> viewModel.updateEventBeginTime(hour, minute) },
            eventBeginTime.hour,
            eventBeginTime.minute,
            true
        ).show()
    }

    private fun showEndTimePicker() {
        TimePickerDialog(
            requireContext(),
            { _, hour, minute -> viewModel.updateEventEndTime(hour, minute) },
            eventEndTime.hour,
            eventEndTime.minute,
            true
        ).show()
    }

    private fun openParticipantsAdding() {
        findNavController().navigate(actionAddingFragmentToParticipantsFragment())
    }

    private fun openLocationPicker() {
        findNavController().navigate(actionAddingFragmentToLocationFragment(null))
    }

    private fun addEvent() {
        with(binding) {
            viewModel.addEvent(
                title = switchIfEmpty(
                    titleInput.text.toString(),
                    getString(R.string.no_title)
                ),
                description = switchIfEmpty(
                    descriptionInput.text.toString(),
                    getString(R.string.no_description)
                ),
                repeatMode = when (repeatButtonsGroup.checkedButtonId) {
                    R.id.repeatDayToggle -> RepeatMode.DAY
                    R.id.repeatWeekToggle -> RepeatMode.WEEK
                    R.id.repeatMonthToggle -> RepeatMode.MONTH
                    R.id.repeatYearToggle -> RepeatMode.YEAR
                    else -> RepeatMode.NEVER
                }
            )
        }
    }

    companion object {

        fun newInstance(): CreationFragment {
            return CreationFragment()
        }
    }
}
