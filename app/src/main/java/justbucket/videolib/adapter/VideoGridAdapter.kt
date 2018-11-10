package justbucket.videolib.adapter

import android.graphics.drawable.Drawable
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import justbucket.videolib.R
import justbucket.videolib.graphics.SelectorCheckDrawable
import justbucket.videolib.model.VideoPres
import kotlinx.android.synthetic.main.recycler_video_card.view.*

class VideoGridAdapter(private val width: Int,
                       private val height: Int,
                       private val requestManager: RequestManager,
                       private val viewHolderListener: ViewHolderListener,
                       private val items: List<VideoPres>)
    : RecyclerView.Adapter<VideoGridAdapter.VideoHolder>() {

    interface ViewHolderListener {

        fun onLoadCompleted(view: ImageView, adapterPosition: Int)

        fun onItemClicked(view: View, adapterPosition: Int)

        fun onItemLongClicked()
    }

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

            selectorCheckDrawable.setSelected(videoPres.selected, false)

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