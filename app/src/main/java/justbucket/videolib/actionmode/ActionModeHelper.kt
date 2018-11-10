package justbucket.videolib.actionmode

import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import justbucket.videolib.R

class ActionModeHelper(private val activity: AppCompatActivity) : ActionMode.Callback {

    private var mode: ActionMode? = null
    private var onActionEventListener: OnActionEventListener? = null

    fun startActionMode() {
        activity.startSupportActionMode(this)
        activity.window.statusBarColor = ContextCompat.getColor(activity, android.R.color.background_dark)

    }

    fun isInActionMode(): Boolean {
        return mode != null
    }

    fun stopActionMode() {
        mode?.finish()
        activity.window?.statusBarColor = ContextCompat.getColor(activity, R.color.colorPrimaryDark)
    }

    fun setActionClickListener(onActionEventListener: OnActionEventListener) {
        this.onActionEventListener = onActionEventListener
    }

    override fun onActionItemClicked(actionMode: ActionMode, item: MenuItem): Boolean {
        onActionEventListener?.onActionItemClicked(actionMode, item)
        return true
    }

    override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
        mode = actionMode
        actionMode.menuInflater?.inflate(R.menu.menu_action_mode, menu)
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
        return true
    }

    override fun onDestroyActionMode(actionMode: ActionMode) {
        onActionEventListener?.onActionModeDestroyed()
        mode = null
        onActionEventListener = null
    }

    interface OnActionEventListener {

        fun onActionItemClicked(mode: ActionMode, item: MenuItem)

        fun onActionModeDestroyed()
    }
}