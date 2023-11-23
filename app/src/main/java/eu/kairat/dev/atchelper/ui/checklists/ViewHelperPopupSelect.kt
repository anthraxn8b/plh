package eu.kairat.dev.atchelper.ui.checklists

import android.app.AlertDialog
import android.content.Context
import android.util.Log

class ViewHelperPopupSelect {
    companion object {
        private const val logTag = "VIEW HELPER POPUP SELECT"

        fun showSelectList(
            context: Context?,
            popupTitle: String,
            items: List<String>,
            callback: (Int) -> Unit
        ) {
            AlertDialog.Builder(context)
                .setTitle(popupTitle)
                .setItems(items.map { it.uppercase() }.toTypedArray()) { dialog, which ->
                    dialog.dismiss()
                    Log.d(logTag, "SELECTED: INDEX: $which")
                    callback(which)
                }
                .show()
        }

        fun showYesNo(
            context: Context?,
            popupTitle: String,
            message: String,
            yes: String,
            no: String,
            callbackYes: () -> Unit,
            callbackNo: () -> Unit
        ) {
            AlertDialog.Builder(context)
                .setTitle(popupTitle)
                .setMessage(message)
                .setPositiveButton(yes) { dialog, _ ->
                    Log.d(logTag, "PRESSED: YES")
                    dialog.dismiss()
                    callbackYes()
                }
                .setNegativeButton(no) { dialog, _ ->
                    Log.d(logTag, "PRESSED: NO")
                    dialog.dismiss()
                    callbackNo()
                }
                .show()
        }

    }
}