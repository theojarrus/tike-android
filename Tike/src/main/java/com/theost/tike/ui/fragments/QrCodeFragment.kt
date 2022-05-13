package com.theost.tike.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theost.tike.R
import com.theost.tike.databinding.FragmentQrCodeBinding
import com.theost.tike.ui.extensions.loadQR
import com.theost.tike.ui.utils.ResUtils.getAttrColor

class QrCodeFragment : BottomSheetDialogFragment() {

    private val args: QrCodeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentQrCodeBinding.inflate(layoutInflater)

        binding.qrCodeView.loadQR(
            args.content,
            getAttrColor(requireContext(), R.attr.colorPrimary)
        )

        return binding.root
    }
}
