package com.theost.tike.feature.auth.ui

import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.common.extension.addBackPressedCallback
import com.theost.tike.common.extension.changeText
import com.theost.tike.common.extension.pressBack
import com.theost.tike.common.recycler.base.BaseAdapter
import com.theost.tike.core.component.model.StateStatus.Initial
import com.theost.tike.core.component.model.StateStatus.Success
import com.theost.tike.core.component.model.StateViews
import com.theost.tike.core.component.ui.BaseStateFragment
import com.theost.tike.core.recycler.lifestyle.LifestyleAdapterDelegate
import com.theost.tike.databinding.FragmentSignUpBinding
import com.theost.tike.domain.model.core.User
import com.theost.tike.domain.model.multi.AuthStatus.SigningUp
import com.theost.tike.feature.auth.presentation.AuthViewModel
import com.theost.tike.feature.auth.presentation.SignUpState
import com.theost.tike.feature.auth.presentation.SignUpViewModel

class SignUpFragment : BaseStateFragment<SignUpState, SignUpViewModel>(R.layout.fragment_sign_up) {

    private val authViewModel: AuthViewModel by activityViewModels()

    private val binding: FragmentSignUpBinding by viewBinding()
    private val adapter: BaseAdapter = BaseAdapter()

    override val viewModel: SignUpViewModel by viewModels()

    override val isHandlingState: Boolean = true
    override val isLoadingEndless: Boolean = true
    override val isRefreshingErrorOnly: Boolean = true

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            viewModel.signOut()
        }
    }

    override fun onResume() {
        super.onResume()
        backPressedCallback.isEnabled = true
    }

    override fun onStop() {
        super.onStop()
        backPressedCallback.isEnabled = false
    }

    override fun setupView() = with(binding) {
        activity.addBackPressedCallback(backPressedCallback)
        toolbar.setNavigationOnClickListener { activity.pressBack() }

        nameInput.addTextChangedListener { if (nameInput.hasFocus()) nameLayout.error = null }
        nickInput.addTextChangedListener { if (nickInput.hasFocus()) nickLayout.error = null }

        signUp.setOnClickListener {
            viewModel.signUp(
                nameInput.text.toString(),
                nickInput.text.toString()
            )
        }

        recycler.adapter = adapter.apply {
            addDelegate(LifestyleAdapterDelegate { viewModel.updateLifestyle(it) })
        }
    }

    override fun render(state: SignUpState) = with(binding) {
        if (state.status == Success) authViewModel.updateAuthStatus(state.authStatus)
        if (state.isNameError || !nameInput.hasFocus()) nameInput.changeText(state.user.name)
        if (state.isNickError || !nickInput.hasFocus()) nickInput.changeText(state.user.nick)
        nameLayout.error = if (state.isNameError) getString(R.string.error_name_format) else null
        nickLayout.error = getNickError(state.isUserExist, state.isNickError)
        lifestylesTitle.isGone = state.lifestyles.isEmpty()
        adapter.submitList(state.lifestyles)
    }

    override val stateViews: StateViews
        get() = StateViews(
            rootView = binding.root,
            actionView = binding.signUp,
            loadingView = binding.loadingView,
            disabledAdapter = adapter,
            disabledViews = listOf(binding.nameInput, binding.nickInput),
        )

    override val initialState: SignUpState
        get() = SignUpState(
            status = Initial,
            authStatus = SigningUp,
            user = User(
                uid = "",
                name = "",
                nick = "",
                email = "",
                phone = "",
                avatar = "",
                friends = emptyList(),
                requesting = emptyList(),
                pending = emptyList(),
                blocked = emptyList(),
                lifestyles = emptyList()
            ),
            lifestyles = emptyList(),
            userLifestyles = emptyList(),
            isUserExist = false,
            isNameError = false,
            isNickError = false
        )

    override val initialAction: SignUpViewModel.() -> Unit = {
        fetchUser()
        fetchLifestyles()
    }

    private fun getNickError(isUserExist: Boolean, isNickError: Boolean): String? {
        return when {
            isUserExist -> getString(R.string.error_nick_exist)
            isNickError -> getString(R.string.error_nick_format)
            else -> null
        }
    }

    companion object {

        fun newInstance(): SignUpFragment {
            return SignUpFragment()
        }
    }
}
