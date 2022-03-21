package com.theost.tike.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.theost.tike.R
import com.theost.tike.data.extensions.getNavigationResult
import com.theost.tike.data.extensions.removeNavigationResult
import com.theost.tike.data.models.state.RepeatMode
import com.theost.tike.data.models.state.Status
import com.theost.tike.data.models.ui.ListAddButton
import com.theost.tike.databinding.FragmentCreationBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.adapters.delegates.AddButtonAdapterDelegate
import com.theost.tike.ui.adapters.delegates.ParticipantAdapterDelegate
import com.theost.tike.ui.interfaces.ActionsHolder
import com.theost.tike.ui.interfaces.DelegateItem
import com.theost.tike.ui.interfaces.EventListener
import com.theost.tike.ui.viewmodels.CreationViewModel
import com.theost.tike.utils.DateUtils
import com.theost.tike.utils.DisplayUtils
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

class CreationFragment : Fragment() {

    private var addedIds: List<String> = emptyList()

    private var eventDate = LocalDate.now()
    private var eventBeginTime = LocalTime.now()
    private var eventEndTime = LocalTime.now()

    private val adapter: BaseAdapter = BaseAdapter()

    private val viewModel: CreationViewModel by viewModels()

    private var _binding: FragmentCreationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreationBinding.inflate(layoutInflater, container, false)

        binding.dateDayInput.setOnClickListener { showDatePicker() }
        binding.dateBeginInput.setOnClickListener { showBeginTimePicker() }
        binding.dateEndInput.setOnClickListener { showEndTimePicker() }
        binding.createEventButton.setOnClickListener { sendEventData() }

        binding.titleInput.addTextChangedListener { updateCreateEventButton() }
        binding.descriptionInput.addTextChangedListener { updateCreateEventButton() }

        binding.titleInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !binding.descriptionInput.hasFocus()) DisplayUtils.hideKeyboard(v)
        }

        binding.descriptionInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !binding.titleInput.hasFocus()) DisplayUtils.hideKeyboard(v)
        }

        binding.participantsList.adapter = adapter.apply {
            addDelegate(ParticipantAdapterDelegate { id -> viewModel.removeParticipant(id) })
            addDelegate(AddButtonAdapterDelegate { openParticipantsAdding() })
        }

        viewModel.participants.observe(viewLifecycleOwner) { participants ->
            addedIds = participants.map { participant -> participant.id }
            adapter.submitList(mutableListOf<DelegateItem>().apply {
                addAll(participants)
                add(ListAddButton())
            })
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

        viewModel.sendingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                Status.Error -> hideLoading()
                Status.Loading -> showLoading()
                Status.Success -> onEventDataSent()
            }
        }

        getNavigationResult<List<String>>(KEY_PARTICIPANTS_REQUEST)?.let { usersIds ->
            removeNavigationResult<List<String>>(KEY_PARTICIPANTS_REQUEST)
            viewModel.loadParticipants(usersIds)
        }

        updateCreateEventButton()

        return binding.root
    }

    private fun showLoading() {
        binding.titleInput.isEnabled = false
        binding.descriptionInput.isEnabled = false
        binding.dateDayInput.isEnabled = false
        binding.dateBeginInput.isEnabled = false
        binding.dateEndInput.isEnabled = false
        binding.repeatButtonsGroup.children.forEach { it.isEnabled = false }
        adapter.setEnabled(false)

        binding.createEventButton.visibility = View.INVISIBLE
        binding.loadingBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.titleInput.isEnabled = true
        binding.descriptionInput.isEnabled = true
        binding.dateDayInput.isEnabled = true
        binding.dateBeginInput.isEnabled = true
        binding.dateEndInput.isEnabled = true
        binding.repeatButtonsGroup.children.forEach { it.isEnabled = true }
        adapter.setEnabled(true)

        binding.createEventButton.visibility = View.VISIBLE
        binding.loadingBar.visibility = View.GONE
    }

    private fun showDatePicker() {
        context?.let { context ->
            DatePickerDialog(
                context,
                { _, year, month, day -> viewModel.updateEventDate(year, month, day) },
                eventDate.year,
                eventDate.monthValue,
                eventDate.dayOfMonth
            ).show()
        }
    }

    private fun showBeginTimePicker() {
        context?.let { context ->
            TimePickerDialog(
                context,
                { _, hour, minute -> viewModel.updateEventBeginTime(hour, minute) },
                eventBeginTime.hour,
                eventBeginTime.minute,
                true
            ).show()
        }
    }

    private fun showEndTimePicker() {
        context?.let { context ->
            TimePickerDialog(
                context,
                { _, hour, minute -> viewModel.updateEventEndTime(hour, minute) },
                eventEndTime.hour,
                eventEndTime.minute,
                true
            ).show()
        }
    }

    private fun openParticipantsAdding() {
        (activity as ActionsHolder).openParticipantsAdding(
            KEY_PARTICIPANTS_REQUEST,
            addedIds.toList()
        )
    }

    private fun checkIsNotEmptyInputFields(): Boolean {
        return binding.titleInput.text.toString().isNotEmpty()
                && binding.descriptionInput.text.toString().isNotEmpty()
    }

    private fun updateCreateEventButton() {
        binding.createEventButton.isEnabled = checkIsNotEmptyInputFields()
    }

    private fun sendEventData() {
        val title = binding.titleInput.text.toString().trim()
        val description = binding.descriptionInput.text.toString().trim()
        val repeatMode = when (binding.repeatButtonsGroup.checkedButtonId) {
            R.id.repeatDayToggle -> RepeatMode.DAY
            R.id.repeatWeekToggle -> RepeatMode.WEEK
            R.id.repeatMonthToggle -> RepeatMode.MONTH
            R.id.repeatYearToggle -> RepeatMode.YEAR
            else -> RepeatMode.NEVER
        }

        viewModel.sendEventData(title = title, description = description, repeatMode = repeatMode)
    }

    private fun onEventDataSent() = (activity as EventListener).onEventCreated(eventDate)

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val KEY_PARTICIPANTS_REQUEST = "request_participants"

        fun newInstance(): Fragment {
            return CreationFragment()
        }
    }

}