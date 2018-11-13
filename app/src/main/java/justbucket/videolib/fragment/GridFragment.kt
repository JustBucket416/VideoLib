package justbucket.videolib.fragment

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.SharedElementCallback
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionInflater
import android.view.*
import com.dekoservidoni.omfm.OneMoreFabMenu
import justbucket.videolib.GridUnit
import justbucket.videolib.ImportActivity
import justbucket.videolib.MainActivity
import justbucket.videolib.R
import justbucket.videolib.actionmode.ActionModeHelper
import justbucket.videolib.di.InjectedFragment
import justbucket.videolib.extension.dialogBox
import justbucket.videolib.extension.myDialog
import justbucket.videolib.model.FilterPres
import justbucket.videolib.model.VideoPres
import justbucket.videolib.viewmodel.GridViewModel
import kotlinx.android.synthetic.main.fragment_grid.*
import kotlinx.android.synthetic.main.recycler_video_card.view.*

/**
 * A [Fragment] subclass which acts as the main application fragment, offering video and
 * tags manipulation. Kinda like god object anti-pattern, in a sense. Should be refactored.
 */
class GridFragment : InjectedFragment<List<VideoPres>, GridViewModel>() {

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
                        viewModel.addTag(box.text.toString())
                        dialog.dismiss()
                    }
                            .setView(box)
                            .show()
                }
                R.id.menu_remove_tag -> {
                    viewModel.getAllTags { tags ->
                        val checkDelete = BooleanArray(tags.size) { false }
                        AlertDialog.Builder(requireContext()).myDialog(getString(R.string.remove_tags),
                                getString(R.string.cancel),
                                getString(R.string.remove)) { dialog, _ ->
                            val tempString = ""
                            val selectedTagList: List<String> = checkDelete.mapIndexed { index, check ->
                                if (check) tags[index]
                                else tempString
                            }.filter { it != tempString }
                            viewModel.deleteTags(selectedTagList)
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
    private lateinit var unit: GridUnit
    private var actionModeHelper: ActionModeHelper? = null
    private var shouldScroll = true

    override val layoutId: Int
        get() = R.layout.fragment_grid
    override val viewModel: GridViewModel
        get() = ViewModelProviders.of(this, viewModelFactory)[GridViewModel::class.java]

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        actionModeHelper = ActionModeHelper(context as AppCompatActivity)
    }

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
                        unit = GridUnit(this@GridFragment, grid_recycler, width, height)
                        if (savedInstanceState != null) {
                            unit.dispatchUpdate(viewModel.getData().value?.data
                                    ?: throw IllegalStateException("Data should not be empty"))
                            (grid_recycler.layoutManager as GridLayoutManager)
                                    .onRestoreInstanceState(savedInstanceState
                                            .getParcelable(RECYCLER_LAYOUT_STATE_KEY))
                            if (savedInstanceState.getBoolean(ACTION_STATE_KEY)) {
                                actionModeHelper?.startActionMode()
                            }
                        } else {
                            viewModel.loadFilter()
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
        //outState.putParcelableArrayList(VIDEO_LIST_KEY, adapter.items)
        outState.putBoolean(ACTION_STATE_KEY, isInActionMode())
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater?.inflate(R.menu.menu_grid, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        viewModel.loadDetailsSwitchState(menu.findItem(R.id.action_change_mode))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_change_mode -> {
                viewModel.modeChanged(item)
                true
            }
            R.id.action_filter_videos -> {
                val filterFragment = FilterFragment.newInstance(viewModel.getFilter())
                filterFragment.show(childFragmentManager, FILTER_DIALOG_TAG)
                childFragmentManager.executePendingTransactions()
                filterFragment.dialog.setOnDismissListener {
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                        with((filterFragment as FilterProvider).getFilter()) {
                            viewModel.saveFilter(this)
                            viewModel.fetchVideos()
                        }
                        filterFragment.dismissAllowingStateLoss()
                    }
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
        if (!this::unit.isInitialized) return
        if (data?.isNotEmpty() == true) {
            grid_recycler.visibility = View.VISIBLE
            grid_text_error.visibility = View.GONE
            unit.dispatchUpdate(data)
            if (shouldScroll) {
                scrollToPosition()
                shouldScroll = false
            }
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

    fun getSwitchMode() = viewModel.switchMode
    fun startActionMode() = actionModeHelper?.startActionMode()
    fun stopActionMode() = actionModeHelper?.stopActionMode()
    fun isInActionMode() = actionModeHelper?.isInActionMode() == true
    fun setActionItemListener(onActionEventListener: ActionModeHelper.OnActionEventListener) {
        actionModeHelper?.setActionClickListener(onActionEventListener)
    }

    fun chooseTags(items: ArrayList<VideoPres>) {
        val selectTagsFragment = SelectTagsFragment.newInstance(items)
        selectTagsFragment.show(childFragmentManager, ACTION_DIALOG_TAG)
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

    fun deleteVideos(list: List<VideoPres>) {
        viewModel.deleteVideos(list)
    }

    interface FilterProvider {
        fun getFilter(): FilterPres
    }
}