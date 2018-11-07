package justbucket.videolib.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import justbucket.videolib.R

class TagListAdapter(private val items: List<String>,
                     private val tagHolderListener: TagHolderListener)
    : RecyclerView.Adapter<TagListAdapter.TagHolder>() {

    private lateinit var selectedItems: BooleanArray

    interface TagHolderListener {

        fun onTagCheckChange(tag: String, checked: Boolean)
    }

    init {
        parseSelected(emptyList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_tag_check, parent, false)
        return TagHolder(view, selectedItems, tagHolderListener)
    }

    override fun onBindViewHolder(holder: TagHolder, position: Int) {
        holder.onBind(items[position], selectedItems[position])
    }

    override fun getItemCount() = items.size

    fun parseSelected(tags: List<String>) {
        this.selectedItems = BooleanArray(items.size) { tags.contains(items[it]) }
        notifyDataSetChanged()
    }

    class TagHolder(itemView: View,
                    private val selectedItems: BooleanArray,
                    private val tagHolderListener: TagHolderListener)
        : RecyclerView.ViewHolder(itemView) {

        private val checkBox = itemView as CheckBox

        fun onBind(tag: String, checked: Boolean) {
            checkBox.text = tag
            checkBox.isChecked = checked
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                selectedItems[adapterPosition] = isChecked
                tagHolderListener.onTagCheckChange(checkBox.text.toString(), isChecked)
            }
        }
    }
}