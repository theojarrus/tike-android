package com.theost.tike.feature.profile.ui

import android.content.Intent
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.extension.load
import com.theost.tike.common.extension.navigate
import com.theost.tike.common.extension.newPlaintextShare
import com.theost.tike.common.extension.pressBack
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.common.recycler.element.button.ButtonAdapterDelegate
import com.theost.tike.common.util.DisplayUtils.showError
import com.theost.tike.core.model.StateStatus.Initial
import com.theost.tike.core.model.StateViews
import com.theost.tike.core.ui.BaseStateFragment
import com.theost.tike.databinding.FragmentProfileBinding
import com.theost.tike.feature.profile.presentation.ProfileState
import com.theost.tike.feature.profile.presentation.ProfileViewModel
import com.theost.tike.feature.profile.ui.ProfileFragmentDirections.Companion.actionProfileToFriends
import com.theost.tike.feature.profile.ui.ProfileFragmentDirections.Companion.actionProfileToPreferences
import com.theost.tike.feature.profile.ui.ProfileFragmentDirections.Companion.actionProfileToQr
import com.theost.tike.feature.profile.ui.mapper.FriendActionMapper
import com.theost.tike.feature.profile.ui.model.ProfileAction
import com.theost.tike.feature.profile.ui.model.ProfileAction.*
import com.theost.tike.feature.profile.ui.recycler.ProfileButtonsFactory
import com.theost.tike.feature.profile.ui.widget.ToolbarMenuAdapter
import java.lang.String.format

class ProfileFragment : BaseStateFragment<ProfileState, ProfileViewModel>(
    R.layout.fragment_profile
) {

    private val args: ProfileFragmentArgs by navArgs()
    private val binding: FragmentProfileBinding by viewBinding()

    private val buttonsFactory = ProfileButtonsFactory()
    private val actionMapper = FriendActionMapper()

    private val toolbarAdapter by lazy { ToolbarMenuAdapter(binding.toolbar) { dispatch(it) } }
    private val controlAdapter = BaseAdapter()
    private val actionAdapter = BaseAdapter()

    override val viewModel: ProfileViewModel by viewModels()

    override val isHandlingState: Boolean = true
    override val isRefreshingErrorOnly: Boolean = true

    private val uid: String?
        get() = args.uid ?: viewModel.state.value?.profile?.uid

    private val nick: String?
        get() = viewModel.state.value?.profile?.nick

    override fun setupView() = with(binding) {
        toolbar.setNavigationOnClickListener { activity.pressBack() }
        val recyclerViews = listOf(Pair(controls, controlAdapter), Pair(actions, actionAdapter))
        recyclerViews.forEach { (recyclerView, adapter) ->
            recyclerView.itemAnimator = null
            recyclerView.adapter = adapter.apply {
                addDelegate(ButtonAdapterDelegate<ProfileAction> { dispatch(it) })
            }
        }
    }

    override fun render(state: ProfileState): Unit = with(binding) {
        val hasAccess = state.profile?.hasAccess != false
        name.text = state.profile?.name ?: getString(R.string.no_user)
        nick.text = state.profile?.nick ?: getString(R.string.no_nick)
        accessView.isGone = hasAccess
        blockedView.isGone = state.profile?.isBlocked != true
        actions.isGone = state.profile?.isActive != true || state.profile.isActual == true
        toolbarAdapter.update(
            status = state.status,
            isActive = state.profile?.isActive,
            isActual = state.profile?.isActual,
            isBlocked = state.profile?.isBlocked
        )
        controlAdapter.submitList(
            buttonsFactory.getControlButtons(
                isActive = state.profile?.isActive,
                isActual = state.profile?.isActual
            )
        )
        actionAdapter.submitList(
            buttonsFactory.getActionButtons(
                isActive = state.profile?.isActive,
                isActual = state.profile?.isActual,
                isBlocked = state.profile?.isBlocked,
                hasAccess = state.profile?.hasAccess,
                friendStatus = state.profile?.friendStatus
            )
        )
        when {
            state.profile?.avatar != null && hasAccess -> avatar.load(state.profile.avatar)
            state.profile?.avatar != null && !hasAccess -> avatar.load(R.drawable.ic_blocked)
            else -> avatar.load(R.drawable.ic_deleted)
        }
    }

    private fun dispatch(action: ProfileAction) {
        when (action) {
            Friends -> navigate(actionProfileToFriends())
            Preferences -> navigate(actionProfileToPreferences(nick))
            Qr -> nick?.let { navigate(actionProfileToQr(it)) }
            Share -> nick?.let { shareProfile(it) }
            else -> uid?.let { actionMapper(action, it) }
                ?.let { viewModel.dispatchFriendAction(it) }
                ?: showError(requireContext(), R.string.feature_not_ready)
        }
    }

    private fun shareProfile(uid: String) {
        val description = format(getString(R.string.share_profile), uid)
        val intent = Intent().newPlaintextShare(getString(R.string.share), description)
        startActivity(intent)
    }

    override val stateViews: StateViews
        get() = StateViews(
            swipeRefresh = binding.swipeRefresh,
            loadingView = binding.loadingView.root,
            errorView = binding.errorView,
            disabledAdapter = actionAdapter
        )

    override val initialState: ProfileState
        get() = ProfileState(
            status = Initial,
            profile = null
        )

    override val initialAction: ProfileViewModel.() -> Unit = {
        observeProfile(args.uid)
    }
}
