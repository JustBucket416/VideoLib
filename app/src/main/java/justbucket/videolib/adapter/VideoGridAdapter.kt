package justbucket.videolib.adapter

import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.transition.TransitionSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import justbucket.videolib.MainActivity
import justbucket.videolib.R
import justbucket.videolib.VideoActivity
import justbucket.videolib.domain.model.SwitchValues
import justbucket.videolib.extension.inTransaction
import justbucket.videolib.fragment.GridFragment
import justbucket.videolib.fragment.ImagePagerFragment
import justbucket.videolib.graphics.SelectorCheckDrawable
import justbucket.videolib.model.VideoPres
import justbucket.videolib.service.MediaPlayerService
import justbucket.videolib.viewmodel.GridViewModel
import kotlinx.android.synthetic.main.recycler_video_card.view.*
import java.util.concurrent.atomic.AtomicBoolean

class VideoGridAdapter(fragment: GridFragment, private val width: Int, private val height: Int,
                       private val items: ArrayList<VideoPres>)
    : RecyclerView.Adapter<VideoGridAdapter.VideoHolder>() {

    interface ViewHolderListener {

        fun onLoadCompleted(view: ImageView, adapterPosition: Int)

        fun onItemClicked(view: View, adapterPosition: Int)

        fun onItemLongClicked()
    }

    private val viewHolderListener = ViewHolderListenerImpl(fragment, items, fragment.ActionModeCallback(items))
    private val requestManager = Glide.with(fragment)

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_video_card, parent, false)
        return VideoHolder(view, width, height, viewHolderListener, requestManager)
    }


    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        holder.onBind(position, items[position])
    }

    override fun getItemCount() = items.size

    override fun getItemId(position: Int) = items[position].id

    override fun onViewRecycled(holder: VideoHolder) {
        super.onViewRecycled(holder)
        holder.image.setImageDrawable(null)
        requestManager.clear(holder.image)
    }

    fun updateItems(newItems: List<VideoPres>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolderListenerImpl(private val fragment: GridFragment,
                                 private val items: ArrayList<VideoPres>,
                                 private val callback: ActionMode.Callback) : ViewHolderListener {

        private val enterTransitionStarted = AtomicBoolean()

        override fun onLoadCompleted(view: ImageView, adapterPosition: Int) {
            Log.i("image-loaded", "$adapterPosition - ${enterTransitionStarted.get()}")
            // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
            if (MainActivity.currentPosition != adapterPosition) {
                return
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return
            }
            fragment.startPostponedEnterTransition()
        }

        override fun onItemClicked(view: View, adapterPosition: Int) {
            if (MainActivity.actionMode != null) {
                items[adapterPosition].selected = !items[adapterPosition].selected
                (view.foreground as SelectorCheckDrawable).setSelected(items[adapterPosition].selected, true)
                if (items.all { !it.selected }) {
                    MainActivity.actionMode?.finish()
                }
            } else {
                // Update the position.
                MainActivity.currentPosition = adapterPosition
                when ((fragment.viewModel as GridViewModel).switchMode) {
                    SwitchValues.SWITCH_OPEN_DETAILS -> {
                        // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
                        // instead of fading out with the rest to prevent an overlapping animation of fade and move).
                        (fragment.exitTransition as TransitionSet).excludeTarget(view, true)

                        val transitioningView = view.recycler_video_image
                        transitioningView.transitionName = items[adapterPosition].id.toString()
                        fragment.fragmentManager?.inTransaction {
                            setReorderingAllowed(true) // Optimize for shared element transition
                                    .addSharedElement(transitioningView, transitioningView.transitionName)
                                    .replace(R.id.fragment_container, ImagePagerFragment.newInstance(items), ImagePagerFragment::class.java
                                            .simpleName)
                                    .addToBackStack(null)
                        }
                    }
                    SwitchValues.SWITCH_PLAY_VIDEO -> {
                        fragment.startActivity(VideoActivity.newIntent(fragment.context, items))

                    }
                    SwitchValues.SWITCH_PLAY_AUDIO -> {
                        fragment.activity?.startService(
                                MediaPlayerService.newUnboundIntent(fragment.context,
                                        ArrayList(items), adapterPosition))
                    }
                }
            }
        }

        override fun onItemLongClicked() {
            MainActivity.actionMode = (fragment.activity as AppCompatActivity).startSupportActionMode(callback)
        }
    }

    class VideoHolder(itemView: View,
                      width: Int,
                      height: Int,
                      private val viewHolderListener: ViewHolderListener,
                      private val requestManager: RequestManager) : RecyclerView.ViewHolder(itemView) {
        private val selectorCheckDrawable = SelectorCheckDrawable()
        private val cardView: CardView = itemView as CardView
        internal val image = itemView.recycler_video_image

        init {
            cardView.layoutParams = ViewGroup.LayoutParams(width, height)
            cardView.setOnClickListener { viewHolderListener.onItemClicked(it, adapterPosition) }
        }

        fun onBind(position: Int, videoPres: VideoPres) {

            itemView.foreground = selectorCheckDrawable

            // Set the string value of the image resource as the unique transition name for the view.
            image.transitionName = videoPres.id.toString()

            // Load the image with Glide to prevent OOM error when the image drawables are very large.
            requestManager
                    .load(videoPres.thumbPath)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any,
                                                  target: Target<Drawable>,
                                                  isFirstResource: Boolean): Boolean {
                            viewHolderListener.onLoadCompleted(image, adapterPosition)
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any,
                                                     target: Target<Drawable>,
                                                     dataSource: DataSource,
                                                     isFirstResource: Boolean): Boolean {
                            viewHolderListener.onLoadCompleted(image, adapterPosition)
                            return false
                        }
                    })
                    .into(object : SimpleTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable,
                                                     transition: Transition<in Drawable>?) {
                            if (position == adapterPosition) {
                                image.setImageDrawable(resource)
                            }
                        }
                    })

            cardView.setOnLongClickListener {
                setChecked(true)
                videoPres.selected = true
                viewHolderListener.onItemLongClicked()
                true
            }
        }

        fun setChecked(checked: Boolean) {
            selectorCheckDrawable.setSelected(checked, true)
        }

    }
}