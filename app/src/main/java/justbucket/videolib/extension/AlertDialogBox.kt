package justbucket.videolib.extension

import android.content.DialogInterface
import android.support.v7.app.AlertDialog

/**
 * A [AlertDialog.Builder] extension function
 */
fun AlertDialog.Builder.myDialog(title: String, negativeText: String, positiveText: String, positiveListener: (DialogInterface, Int) -> Unit): AlertDialog.Builder {
    return setTitle(title)
            .setNegativeButton(negativeText) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(positiveText, positiveListener)
}