package justbucket.videolib.extension

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

/**
 * An inline function to simplify fragment transactions
 */
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) =
        beginTransaction().func().commit()