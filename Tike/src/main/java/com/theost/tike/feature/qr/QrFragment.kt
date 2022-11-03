package com.theost.tike.feature.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theost.tike.R
import com.theost.tike.common.extension.loadQR
import com.theost.tike.common.util.ResUtils.getAttrColor
import com.theost.tike.databinding.FragmentQrBinding

class QrFragment : BottomSheetDialogFragment() {

    private val args: QrFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentQrBinding.inflate(layoutInflater).run {
            imageView.loadQR(args.content, getAttrColor(requireContext(), R.attr.colorPrimary))
            root
        }
    }
}
