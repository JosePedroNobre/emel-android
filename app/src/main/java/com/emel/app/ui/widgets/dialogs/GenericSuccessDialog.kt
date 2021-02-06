package com.emel.app.ui.widgets.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.emel.app.R
import kotlinx.android.synthetic.main.dialog_generic_success.*

class GenericSuccessDialog : DialogFragment() {

    companion object {
        val TAG = GenericSuccessDialog::class.java.canonicalName
        private const val ARG_TEXT_TITLE = "ARG_TEXT_TITLE"
        private const val ARG_TEXT_BODY = "ARG_TEXT_BODY"
        fun newInstance(textTitle: String, textBody: String) = GenericSuccessDialog().apply {
            arguments = Bundle().apply {
                putString(ARG_TEXT_TITLE, textTitle)
                putString(ARG_TEXT_BODY, textBody)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(activity!!, theme) {
            override fun onBackPressed() {
                // this prevents the user from back press while the dialog is up
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setDimAmount(0F)
        return inflater.inflate(R.layout.dialog_generic_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.getString(ARG_TEXT_BODY)?.let {
            dialogSuccessBody.text = it
        }

        arguments?.getString(ARG_TEXT_TITLE)?.let {
            dialogSuccessTitle.text = it
        }
    }
}