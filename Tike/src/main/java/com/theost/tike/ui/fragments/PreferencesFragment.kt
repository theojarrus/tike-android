package com.theost.tike.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.View.OVER_SCROLL_NEVER
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthProvider
import com.theost.tike.R
import com.theost.tike.ui.fragments.SettingsFragmentDirections.Companion.actionSettingsFragmentToBlacklistFragment
import com.theost.tike.ui.utils.AuthUtils.getSignInIntent
import com.theost.tike.ui.utils.PrefUtils.PREF_KEY_ACCOUNT_BLACKLIST
import com.theost.tike.ui.utils.PrefUtils.PREF_KEY_ACCOUNT_DELETE
import com.theost.tike.ui.utils.PrefUtils.PREF_KEY_ACCOUNT_SIGN_OUT
import com.theost.tike.ui.viewmodels.PreferencesViewModel

class PreferencesFragment : PreferenceFragmentCompat() {

    private val authHandler = registerForActivityResult(StartActivityForResult()) { onAuth(it) }
    private val viewModel: PreferencesViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_preferences, rootKey)
        arguments?.getString(ARG_PREFERENCES_ID)?.let { id ->
            findPreference<Preference>(PREF_KEY_ACCOUNT_SIGN_OUT)?.summary = id
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.overScrollMode = OVER_SCROLL_NEVER

        findPreference<Preference>(PREF_KEY_ACCOUNT_BLACKLIST)?.setOnPreferenceClickListener {
            findNavController().navigate(actionSettingsFragmentToBlacklistFragment())
            true
        }

        findPreference<Preference>(PREF_KEY_ACCOUNT_SIGN_OUT)?.setOnPreferenceClickListener {
            viewModel.signOut()
            true
        }

        findPreference<Preference>(PREF_KEY_ACCOUNT_DELETE)?.setOnPreferenceClickListener {
            activity?.let { authHandler.launch(getSignInIntent(it)) }
            true
        }
    }

    private fun onAuth(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            GoogleSignIn.getSignedInAccountFromIntent(result.data).result.idToken
                ?.let { viewModel.delete(GoogleAuthProvider.getCredential(it, null)) }
                ?: makeText(requireContext(), getString(R.string.error_delete), LENGTH_SHORT).show()
        }
    }

    companion object {

        private const val ARG_PREFERENCES_ID = "id"

        fun newInstance(id: String? = null): Fragment {
            return PreferencesFragment().apply {
                arguments = bundleOf(Pair(ARG_PREFERENCES_ID, id))
            }
        }
    }
}
