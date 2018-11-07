package justbucket.videolib.extension

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout

/**
 * A simple [EditText]
 */
fun dialogBox(context: Context, textHint: String): EditText {
    return EditText(context).apply {
        hint = textHint
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
    }
}