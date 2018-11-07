package justbucket.videolib.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import justbucket.videolib.R
import justbucket.videolib.graphics.SelectorCheckDrawable

class SourceImageAdapter(private val items: List<Int>,
                         private val listener: SourceClickListener)
    : RecyclerView.Adapter<SourceImageAdapter.SourceHolder>() {

    private lateinit var checked: BooleanArray

    interface SourceClickListener {

        fun onSourceClicked(source: Int, updater: (Boolean) -> Unit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceHolder {
        return SourceHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_source, parent, false))
    }

    override fun onBindViewHolder(holder: SourceHolder, position: Int) {
        holder.onBind(items[position], checked[position], listener)
    }

    override fun getItemCount() = items.size

    fun setCheckedSources(checkedSources: List<Int>) {
        checked = BooleanArray(items.size) { checkedSources.contains(items[it]) }
    }

    class SourceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image = itemView as ImageView
        private val selectorCheckDrawable = SelectorCheckDrawable()

        fun onBind(source: Int, checked: Boolean, sourceClickListener: SourceClickListener) {

            Glide.with(itemView.context)
                    .load(source)
                    .into(image)

            itemView.foreground = selectorCheckDrawable

            setSelected(checked)

            itemView.setOnClickListener {
                sourceClickListener.onSourceClicked(source, this::setSelected)
            }
        }

        private fun setSelected(selected: Boolean) {
            selectorCheckDrawable.setSelected(selected, true)
        }
    }
}