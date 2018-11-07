package justbucket.videolib.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.SharedElementCallback
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionInflater
import android.view.*
import com.dekoservidoni.omfm.OneMoreFabMenu
import justbucket.videolib.ImportActivity
import justbucket.videolib.MainActivity
import justbucket.videolib.R
import justbucket.videolib.adapter.VideoGridAdapter
import justbucket.videolib.di.InjectedFragment
import justbucket.videolib.extension.dialogBox
import justbucket.videolib.extension.myDialog
import justbucket.videolib.model.FilterPres
import justbucket.videolib.model.VideoPres
import justbucket.videolib.viewmodel.BaseViewModel
import justbucket.videolib.viewmodel.GridViewModel
import kotlinx.android.synthetic.main.fragment_grid.*
import kotlinx.android.synthetic.main.recycler_video_card.view.*

/**
 * A [Fragment] subclass which acts as the main application fragment, offering video and
 * tags manipulation. Kinda like god object anti-pattern, in a sense. Should be refactored.
 */
class GridFragment : InjectedFragment<List<VideoPres>>() {

    companion object {
        private const val RECYCLER_LAYOUT_STATE_KEY = "recycler-state-key"
        private const val ACTION_STATE_KEY = "action-state-key"
        private const val FILTER_DIALOG_TAG = "filter"
        private const val ACTION_DIALOG_TAG = "action"
        private const val LANDSCAPE_COLUMN_NUM = 4
        private const val LANDSCAPE_ROW_NUM = 4
        private const val PORTRAIT_COLUMN_NUM = 2
        private const val PORTRAIT_ROW_NUM = 6
    }


    private val fabMenuListener = object : OneMoreFabMenu.OptionsClick {
        override fun onOptionClick(optionId: Int?) {
            when (optionId) {
                R.id.menu_add_tag -> {
                    val box = dialogBox(requireContext(), getString(R.string.separate_tags))
                    AlertDialog.Builder(requireContext()).myDialog(getString(R.string.add_tags),
                            getString(R.string.cancel),
                            getString(R.string.add)) { dialog: DialogInterface, _ ->
                        (viewModel as GridViewModel).addTag(box.text.toString())
                        dialog.dismiss()
                    }
                            .setView(box)
                            .show()
                }
                R.id.menu_remove_tag -> {
                    (viewModel as GridViewModel).getAllTags { tags ->
                        val checkDelete = BooleanArray(tags.size) { false }
                        AlertDialog.Builder(requireContext()).myDialog(getString(R.string.remove_tags),
                                getString(R.string.cancel),
                                getString(R.string.remove)) { dialog, _ ->
                            for ((index, bool) in checkDelete.withIndex()) {
                                if (bool) (viewModel as GridViewModel).deleteTag(tags[index])
                            }
                            dialog.dismiss()
                        }
                                .setMultiChoiceItems(tags.toTypedArray(), checkDelete) { _, which, isChecked ->
                                    checkDelete[which] = isChecked
                                }
                                .show()
                    }
                }
                R.id.menu_add_video -> {
                    val box = dialogBox(requireContext(), getString(R.string.type_video_link))
                    AlertDialog.Builder(requireContext()).myDialog(getString(R.string.add_video),
                            getString(R.string.cancel), getString(R.string.parse)) { dialog, _ ->
                        startActivity(ImportActivity.newIntent(context, box.text.toString()))
                        dialog.dismiss()
                    }
                            .setView(box)
                            .show()
                }
            }
        }
    }

    private lateinit var adapter: VideoGridAdapter
    override val layoutId: Int
        get() = R.layout.fragment_grid

    override val viewModel: BaseViewModel<List<VideoPres>>
        get() = ViewModelProviders.of(this, viewModelFactory)[GridViewModel::class.java]

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        postponeEnterTransition()
        prepareTransitions()
        grid_recycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.left = 4
                outRect.top = 2
            }
        })
        grid_layout.viewTreeObserver
                .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        grid_layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        val rowCount = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                            LANDSCAPE_ROW_NUM else PORTRAIT_ROW_NUM
                        val colCount = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                            LANDSCAPE_COLUMN_NUM else PORTRAIT_COLUMN_NUM
                        grid_recycler.layoutManager = GridLayoutManager(context, colCount)
                        val width = grid_layout.width / colCount - 4
                        val height = grid_layout.measuredHeight / rowCount - 4
                        adapter = VideoGridAdapter(this@GridFragment, width, height, arrayListOf())
                        grid_recycler.adapter = adapter
                        (viewModel as GridViewModel).getFilter()
                        if (savedInstanceState != null) {
                            if (!savedInstanceState.getBoolean(ACTION_STATE_KEY))
                            //(activity as AppCompatActivity).startSupportActionMode(actionModeCallback)
                                (grid_recycler.layoutManager as RecyclerView.LayoutManager)
                                        .onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLER_LAYOUT_STATE_KEY))
                        }
                    }
                })
        fab.setOptionsClick(fabMenuListener)

        grid_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(grid_recycler: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !fab.isShown)
                    fab.show()
                else if (dy > 0 && fab.isShown)
                    fab.hide()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (grid_recycler?.layoutManager != null) outState.putParcelable(RECYCLER_LAYOUT_STATE_KEY,
                (grid_recycler.layoutManager as RecyclerView.LayoutManager).onSaveInstanceState())
        outState.putBoolean(ACTION_STATE_KEY, MainActivity.actionMode == null)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater?.inflate(R.menu.menu_grid, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        (viewModel as GridViewModel).loadDetailsSwitchState(menu?.findItem(R.id.action_change_mode)!!)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_change_mode -> {
                (viewModel as GridViewModel).modeChanged(item)
                true
            }
            R.id.action_filter_videos -> {
                val filterFragment = FilterFragment.newInstance()
                filterFragment.show(childFragmentManager, FILTER_DIALOG_TAG)
                childFragmentManager.executePendingTransactions()
                filterFragment.dialog.setOnDismissListener {
                    with((filterFragment as FilterProvider).getFilter()) {
                        (viewModel as GridViewModel).saveFilter(this)
                        (viewModel as GridViewModel).fetchVideos(this)
                    }
                    filterFragment.dismissAllowingStateLoss()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Shows successfully loaded videos
     *
     * @param data - loaded videos
     */
    override fun setupForSuccess(data: List<VideoPres>?) {
        if (data?.isNotEmpty() == true) {
            adapter.updateItems(data)
            grid_recycler.visibility = View.VISIBLE
            grid_text_error.visibility = View.GONE
            scrollToPosition()
        } else setupForError(getString(R.string.error_no_videos_found))

    }

    /**
     * Shows the loading UI
     */
    override fun setupForLoading() {
        grid_text_error.visibility = View.GONE
    }

    /**
     * Shows the error UI
     *
     * @param message - the error cause
     */
    override fun setupForError(message: String?) {
        grid_recycler.visibility = View.GONE
        grid_text_error.visibility = View.VISIBLE
        grid_text_error.text = message

    }

    /**
     * Scrolls the recycler view to show the last viewed item in the grid. This is important when
     * navigating back from the grid.
     */
    private fun scrollToPosition() {
        grid_recycler.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View,
                                        left: Int,
                                        top: Int,
                                        right: Int,
                                        bottom: Int,
                                        oldLeft: Int,
                                        oldTop: Int,
                                        oldRight: Int,
                                        oldBottom: Int) {
                grid_recycler.removeOnLayoutChangeListener(this)
                val layoutManager = grid_recycler.layoutManager
                val viewAtPosition = layoutManager?.findViewByPosition(MainActivity.currentPosition)
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                                .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    grid_recycler.post { layoutManager?.scrollToPosition(MainActivity.currentPosition + 4) }
                }
            }
        })
    }

    /**
     * Prepares the shared element transition to the pager fragment, as well as the other transitions
     * that affect the flow.
     */
    private fun prepareTransitions() {
        exitTransition = TransitionInflater.from(context).inflateTransition(R.transition.grid_exit_transition)

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
                val holder = grid_recycler.findViewHolderForAdapterPosition(MainActivity.currentPosition)
                if (holder?.itemView?.recycler_video_image != null) sharedElements[names[0]] = holder.itemView.recycler_video_image
            }
        })
    }

    inner class ActionModeCallback(private val items: ArrayList<VideoPres>) : android.support.v7.view.ActionMode.Callback {

        override fun onActionItemClicked(mode: android.support.v7.view.ActionMode, item: MenuItem): Boolean {
            val lastPos = (grid_recycler.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
            when (item.itemId) {
                R.id.menu_select_all -> for ((position, video) in items.withIndex()) {
                    video.selected = true
                    if (position <= lastPos) (grid_recycler.findViewHolderForAdapterPosition(position)
                            as? VideoGridAdapter.VideoHolder)?.setChecked(true)
                }
                R.id.menu_select_none -> for ((position, video) in items.withIndex()) {
                    video.selected = false
                    if (position <= lastPos) (grid_recycler.findViewHolderForAdapterPosition(position)
                            as? VideoGridAdapter.VideoHolder)?.setChecked(false)
                }
                R.id.menu_invert -> for ((position, video) in items.withIndex()) {
                    val selected = !video.selected
                    video.selected = selected
                    if (position <= lastPos) (grid_recycler.findViewHolderForAdapterPosition(position)
                            as? VideoGridAdapter.VideoHolder)?.setChecked(selected)
                }
                R.id.menu_delete -> {
                    (viewModel as GridViewModel).deleteVideos(items.filter { it.selected })
                    mode.finish()
                }
                R.id.menu_select_tags -> {
                    val selectTagsFragment = SelectTagsFragment.newInstance(items)
                    selectTagsFragment.show(childFragmentManager, ACTION_DIALOG_TAG)
                }
            }
            return true
        }

        override fun onCreateActionMode(mode: android.support.v7.view.ActionMode, menu: Menu): Boolean {
            mode.menuInflater?.inflate(R.menu.menu_action_mode, menu)
            // This is to highlight the status bar and distinguish it from the action bar,
            // as the action bar while in the action mode is colored app_green_dark
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.background_dark)
            return true
        }

        override fun onPrepareActionMode(mode: android.support.v7.view.ActionMode, menu: Menu): Boolean {
            val lastPos = (grid_recycler.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
            for ((position, video) in items.withIndex()) {
                if (position <= lastPos && video.selected)
                    (grid_recycler.findViewHolderForAdapterPosition(position) as?
                            VideoGridAdapter.VideoHolder)?.setChecked(true)
            }
            return true
        }

        override fun onDestroyActionMode(mode: android.support.v7.view.ActionMode) {
            val lastPos = (grid_recycler.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
            for ((position, video) in items.withIndex()) {
                video.selected = false
                if (position <= lastPos)
                    (grid_recycler.findViewHolderForAdapterPosition(position) as? VideoGridAdapter.VideoHolder)
                            ?.setChecked(false)
            }
            MainActivity.actionMode = null
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)

        }
    }

    interface FilterProvider {

        fun getFilter(): FilterPres
    }
}