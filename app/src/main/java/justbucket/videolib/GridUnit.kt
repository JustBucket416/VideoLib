package justbucket.videolib

import android.support.v7.view.ActionMode
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionSet
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import justbucket.videolib.actionmode.ActionModeHelper
import justbucket.videolib.adapter.VideoGridAdapter
import justbucket.videolib.domain.model.SwitchValues
import justbucket.videolib.extension.inTransaction
import justbucket.videolib.fragment.GridFragment
import justbucket.videolib.fragment.ImagePagerFragment
import justbucket.videolib.graphics.SelectorCheckDrawable
import justbucket.videolib.model.VideoPres
import justbucket.videolib.service.MediaPlayerService
import kotlinx.android.synthetic.main.recycler_video_card.view.*
import java.util.concurrent.atomic.AtomicBoolean

class GridUnit(private val gridFragment: GridFragment,
               private val gridRecyclerView: RecyclerView,
               width: Int,
               height: Int) : VideoGridAdapter.ViewHolderListener {
    private val layoutManager = gridRecyclerView.layoutManager as GridLayoutManager
    private val onActionItemClickListener = OnActionEventListenerImpl()
    private val enterTransitionStarted = AtomicBoolean()
    private val items = arrayListOf<VideoPres>()
    private val adapter: VideoGridAdapter = VideoGridAdapter(
            width,
            height,
            Glide.with(gridFragment),
            this,
            items
    )

    init {
        gridRecyclerView.adapter = adapter
        gridFragment.setActionItemListener(onActionItemClickListener)
    }

    override fun onLoadCompleted(view: ImageView, adapterPosition: Int) {
        // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
        if (MainActivity.currentPosition != adapterPosition) {
            return
        }
        if (enterTransitionStarted.getAndSet(true)) {
            return
        }
        gridFragment.startPostponedEnterTransition()
    }

    override fun onItemClicked(view: View, adapterPosition: Int) {
        if (gridFragment.isInActionMode()) {
            items[adapterPosition].selected = !items[adapterPosition].selected
            (view.foreground as SelectorCheckDrawable).setSelected(items[adapterPosition].selected, true)
            if (items.all { !it.selected }) {
                gridFragment.stopActionMode()
            }
        } else {
            // Update the position.
            MainActivity.currentPosition = adapterPosition
            when (gridFragment.getSwitchMode()) {
                SwitchValues.SWITCH_OPEN_DETAILS -> {
                    // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
                    // instead of fading out with the rest to prevent an overlapping animation of fade and move).
                    (gridFragment.exitTransition as TransitionSet).excludeTarget(view, true)

                    val transitioningView = view.recycler_video_image
                    transitioningView.transitionName = items[adapterPosition].id.toString()
                    gridFragment.fragmentManager?.inTransaction {
                        setReorderingAllowed(true) // Optimize for shared element transition
                                .addSharedElement(transitioningView, transitioningView.transitionName)
                                .replace(R.id.fragment_container, ImagePagerFragment.newInstance(items), ImagePagerFragment::class.java
                                        .simpleName)
                                .addToBackStack(null)
                    }
                }
                SwitchValues.SWITCH_PLAY_VIDEO -> {
                    gridFragment.startActivity(VideoActivity.newIntent(gridFragment.context, items))
                }
                SwitchValues.SWITCH_PLAY_AUDIO -> {
                    gridFragment.activity?.startService(
                            MediaPlayerService.newUnboundIntent(gridFragment.context,
                                    ArrayList(items), adapterPosition))
                }
            }
        }
    }

    override fun onItemLongClicked() {
        if (!gridFragment.isInSearchMode) {
            gridFragment.startActionMode()
        }
    }

    fun dispatchUpdate(newItems: List<VideoPres>) {
        items.clear()
        items.addAll(newItems)
        adapter.notifyDataSetChanged()
    }

    private fun updateAllItemsChecked(checked: Boolean) {
        val firstPos = layoutManager.findFirstVisibleItemPosition()
        val lastPos = layoutManager.findLastVisibleItemPosition()
        for ((index, video) in items.withIndex()) {
            video.selected = checked
            if (index in firstPos..lastPos) {
                (gridRecyclerView.findViewHolderForAdapterPosition(index) as VideoGridAdapter.VideoHolder).setChecked(checked)
            }
        }
    }

    private fun invertSelection() {
        val firstPos = layoutManager.findFirstVisibleItemPosition()
        val lastPos = layoutManager.findLastVisibleItemPosition()
        for ((index, video) in items.withIndex()) {
            video.selected = !video.selected
            if (index in firstPos..lastPos) {
                (gridRecyclerView.findViewHolderForAdapterPosition(index) as VideoGridAdapter.VideoHolder).setChecked(video.selected)
            }
        }
    }

    fun resetItems() {
        updateAllItemsChecked(false)
    }

    fun getSelectedVideos(): List<VideoPres> {
        val items = items.filter { it.selected }
        updateAllItemsChecked(false)
        return items
    }

    inner class OnActionEventListenerImpl : ActionModeHelper.OnActionEventListener {
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem) {
            when (item.itemId) {
                R.id.menu_select_all -> updateAllItemsChecked(true)
                R.id.menu_select_none -> updateAllItemsChecked(false)
                R.id.menu_invert -> invertSelection()
                R.id.menu_delete -> {
                    gridFragment.deleteVideos(items.filter { it.selected })
                    mode.finish()
                }
                R.id.menu_select_tags -> gridFragment.chooseTags(items)
            }
        }

        override fun onActionModeDestroyed() {
            updateAllItemsChecked(false)
        }
    }
}