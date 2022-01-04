package com.warmpot.android.stackoverflow.screen.common.dialog

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class InfoDialogFragment : BaseDialogFragment() {

    companion object {
        const val TAG = "InfoDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton(positiveButtonCaption) { dialog, _ ->
                dialog.dismiss()
                dialogListener.onDialogCompleted(DialogResult.Yes(Unit))
            }
            .create()
    }
}