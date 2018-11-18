package justbucket.videolib.extension

import android.content.Context
import android.content.res.Configuration

fun Context.isInHorizontalOrientation() = (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)