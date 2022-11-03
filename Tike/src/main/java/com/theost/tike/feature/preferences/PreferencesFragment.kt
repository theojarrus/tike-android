package com.theost.tike.feature.preferences

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.theost.tike.R
import com.theost.tike.R.layout
import com.theost.tike.common.extension.pressBack
import com.theost.tike.databinding.FragmentPreferencesBinding

class PreferencesFragment : Fragment(layout.fragment_preferences) {

    private val args: PreferencesFragmentArgs by navArgs()
    private val binding: FragmentPreferencesBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { activity.pressBack() }
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PreferencesCompatFragment.newInstance(args.id))
            .commit()
    }
}
