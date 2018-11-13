package justbucket.videolib.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView.VERTICAL
import android.util.Log
import android.view.View
import justbucket.videolib.R
import justbucket.videolib.adapter.TagListAdapter
import justbucket.videolib.di.InjectedDialogFragment
import justbucket.videolib.domain.utils.getOrDie
import justbucket.videolib.model.VideoPres
import justbucket.videolib.viewmodel.ActionViewModel
import kotlinx.android.synthetic.main.fragment_dialog_actionmode.*

/**
 * An [InjectedDialogFragment] subclass that shows tag selection UI
 */
class SelectTagsFragment : InjectedDialogFragment<List<String>, ActionViewModel>() {

    companion object {

        private const val SELECTED_VIDEOS_LIST_KEY = "selected-videos-list-key"

        fun newInstance(videos: ArrayList<VideoPres>): SelectTagsFragment {
            return SelectTagsFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(SELECTED_VIDEOS_LIST_KEY, videos)
                }
            }
        }
    }

    private val tagList = mutableListOf<String>()
    private lateinit var adapter: TagListAdapter

    override val layoutId: Int
        get() = R.layout.fragment_dialog_actionmode

    override val viewModel: ActionViewModel
        get() = ViewModelProviders.of(this, viewModelFactory)[ActionViewModel::class.java]

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selector_button_apply.setOnClickListener {
            if (arguments == null) throw IllegalStateException("arguments are null!")
            val videos = arguments?.getParcelableArrayList<VideoPres>(SELECTED_VIDEOS_LIST_KEY).getOrDie(SELECTED_VIDEOS_LIST_KEY)
            viewModel.applyTags(videos, tagList)
            dismiss()
        }
        selector_button_reset.setOnClickListener { adapter.parseSelected(emptyList()) }
    }

    /**
     * Setups for showing error message
     *
     * @param message - the error cause
     */
    override fun setupForError(message: String?) {
        selector_recycler.visibility = View.VISIBLE
        selector_text_no_tags.visibility = View.GONE
        Log.e(tag, message)
    }

    /**
     * Setups for showing tag list
     *
     * @param data - list with text
     */
    override fun setupForSuccess(data: List<String>?) {
        if (data?.isNotEmpty() == true) {
            adapter = TagListAdapter(data, object : TagListAdapter.TagHolderListener {
                override fun onTagCheckChange(tag: String, checked: Boolean) {
                    if (checked) tagList.add(tag) else tagList.remove(tag)
                }
            })
            adapter.parseSelected(emptyList())
            selector_recycler.adapter = adapter
            selector_recycler.addItemDecoration(DividerItemDecoration(context, VERTICAL))
        } else setupForError(getString(R.string.no_tags))
    }

    override fun setupForLoading() {
        selector_text_no_tags.visibility = View.GONE
    }
}