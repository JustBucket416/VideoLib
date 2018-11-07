package justbucket.videolib.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView.VERTICAL
import android.view.View
import justbucket.videolib.R
import justbucket.videolib.adapter.SourceImageAdapter
import justbucket.videolib.adapter.TagListAdapter
import justbucket.videolib.di.InjectedDialogFragment
import justbucket.videolib.model.FilterPres
import justbucket.videolib.viewmodel.BaseViewModel
import justbucket.videolib.viewmodel.FilterViewModel
import kotlinx.android.synthetic.main.fragment_dialog_filter.*

/**
 * A [DialogFragment] subclass that shows the video filtering UI
 */
class FilterFragment : InjectedDialogFragment<Triple<List<String>, List<Int>, FilterPres>>(),
        GridFragment.FilterProvider {

    companion object {
        fun newInstance(): FilterFragment {
            return FilterFragment()
        }
    }

    private lateinit var tagAdapter: TagListAdapter
    private lateinit var sourceAdapter: SourceImageAdapter
    private lateinit var filterPres: FilterPres

    override val layoutId: Int
        get() = R.layout.fragment_dialog_filter

    override val viewModel: BaseViewModel<Triple<List<String>, List<Int>, FilterPres>>
        get() = ViewModelProviders.of(this, viewModelFactory)[FilterViewModel::class.java]

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        diafrag_recycler_tags.addItemDecoration(DividerItemDecoration(context, VERTICAL))
    }

    override fun setupForSuccess(data: Triple<List<String>, List<Int>, FilterPres>?) {
        val (tags: List<String>, sources: List<Int>, filter: FilterPres) = data!!
        filterPres = filter
        diafrag_toggle.isChecked = filter.isAllAnyCheck
        diafrag_edit_search.setText(filter.text)
        showTags(tags)
        showSources(sources)
    }

    /**
     * Parses the template
     *
     * @return parsed template
     */
    override fun getFilter() = filterPres

    /**
     * Shows successfully loaded videos
     *
     * @param tags - loaded tags
     */
    private fun showTags(tags: List<String>) {
        if (tags.isNotEmpty()) {
            diafrag_recycler_tags.visibility = View.VISIBLE
            diafrag_text_no_tags.visibility = View.GONE
            tagAdapter = TagListAdapter(tags, object : TagListAdapter.TagHolderListener {
                override fun onTagCheckChange(tag: String, checked: Boolean) {
                    if (checked) filterPres.tags.add(tag) else filterPres.tags.remove(tag)
                }
            })
            tagAdapter.parseSelected(filterPres.tags)
            diafrag_recycler_tags.adapter = tagAdapter
        } else setupForError("No tags found")
    }

    private fun showSources(sources: List<Int>) {
        if (sources.isNotEmpty()) {
            sourceAdapter = SourceImageAdapter(sources, object : SourceImageAdapter.SourceClickListener {
                override fun onSourceClicked(source: Int, updater: (Boolean) -> Unit) {
                    if (filterPres.sources.contains(source)) {
                        filterPres.sources.remove(source)
                        updater(false)
                    } else {
                        filterPres.sources.add(source)
                        updater(true)
                    }
                }
            })
            sourceAdapter.setCheckedSources(filterPres.sources)
            diafrag_recycler_sources.adapter = sourceAdapter
        }
    }

    /**
     * Shows the loading UI
     */
    override fun setupForLoading() {
        diafrag_text_no_tags.visibility = View.GONE
    }

    /**
     * Shows the error UI
     *
     * @param message - the error cause
     */
    override fun setupForError(message: String?) {
        diafrag_recycler_tags.visibility = View.GONE
        diafrag_text_no_tags.visibility = View.VISIBLE
        diafrag_text_no_tags.text = message ?: return
    }

}