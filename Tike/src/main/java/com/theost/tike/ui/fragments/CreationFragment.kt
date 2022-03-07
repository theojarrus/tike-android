package com.theost.tike.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.theost.tike.R
import com.theost.tike.data.models.state.RepeatMode
import com.theost.tike.data.models.ui.ListButton
import com.theost.tike.data.models.ui.ListParticipant
import com.theost.tike.data.viewmodels.CreationViewModel
import com.theost.tike.databinding.FragmentCreationBinding
import com.theost.tike.ui.adapters.core.BaseAdapter
import com.theost.tike.ui.adapters.delegates.ButtonAdapterDelegate
import com.theost.tike.ui.adapters.delegates.ParticipantAdapterDelegate
import com.theost.tike.ui.interfaces.DelegateItem
import com.theost.tike.utils.DateUtils
import com.theost.tike.utils.DisplayUtils
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

class CreationFragment : Fragment() {

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
            addDelegate(ButtonAdapterDelegate { openParticipantsAdding() })
        }

        viewModel.participants.observe(viewLifecycleOwner) { participants ->
            adapter.submitList(mutableListOf<DelegateItem>(ListButton()).apply { addAll(participants) })
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

        updateCreateEventButton()

        return binding.root
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
        // todo
    }

    private fun addParticipants(participants: List<ListParticipant>) {
        viewModel.updateParticipants(participants)
    }

    private fun checkIsNotEmptyInputFields(): Boolean {
        return binding.titleInput.text.toString().isNotEmpty()
                && binding.descriptionInput.text.toString().isNotEmpty()
    }

    private fun updateCreateEventButton() {
        binding.createEventButton.isVisible = checkIsNotEmptyInputFields()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance(): Fragment {
            return CreationFragment()
        }
    }

}