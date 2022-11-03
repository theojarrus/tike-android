package com.theost.tike.feature.preferences

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.theost.tike.R.string
import com.theost.tike.R.xml
import com.theost.tike.common.extension.navigate
import com.theost.tike.common.util.AuthUtils.getCredential
import com.theost.tike.common.util.AuthUtils.getSignInIntent
import com.theost.tike.common.util.AuthUtils.getSignedInAccountFromIntent
import com.theost.tike.common.util.DisplayUtils
import com.theost.tike.common.util.PrefUtils.PREF_KEY_ACCOUNT_BLACKLIST
import com.theost.tike.common.util.PrefUtils.PREF_KEY_ACCOUNT_DELETE
import com.theost.tike.common.util.PrefUtils.PREF_KEY_ACCOUNT_SIGN_OUT
import com.theost.tike.feature.auth.presentation.AuthViewModel
import com.theost.tike.feature.preferences.PreferencesFragmentDirections.Companion.actionPreferencesToBlacklist

class PreferencesCompatFragment : PreferenceFragmentCompat() {

    private val authViewModel: AuthViewModel by activityViewModels()

    private val authHandler = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) getSignedInAccountFromIntent(result)?.idToken
            ?.let { authViewModel.deleteAccount(getCredential(it)) }
            ?: DisplayUtils.showError(requireContext(), string.error_auth)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(xml.fragment_preferences, rootKey)
        arguments?.getString(ARG_PREFERENCES_ID)?.let { id ->
            findPreference<Preference>(PREF_KEY_ACCOUNT_SIGN_OUT)?.summary = id
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.overScrollMode = OVER_SCROLL_NEVER

        findPreference<Preference>(PREF_KEY_ACCOUNT_BLACKLIST)?.setOnPreferenceClickListener {
            navigate(actionPreferencesToBlacklist())
            true
        }

        findPreference<Preference>(PREF_KEY_ACCOUNT_SIGN_OUT)?.setOnPreferenceClickListener {
            authViewModel.signOut()
            true
        }

        findPreference<Preference>(PREF_KEY_ACCOUNT_DELETE)?.setOnPreferenceClickListener {
            activity?.let { authHandler.launch(getSignInIntent(it)) }
            true
        }
    }

    companion object {

        private const val ARG_PREFERENCES_ID = "nick"

        fun newInstance(id: String? = null): Fragment {
            return PreferencesCompatFragment().apply {
                arguments = bundleOf(Pair(ARG_PREFERENCES_ID, id))
            }
        }
    }
}
