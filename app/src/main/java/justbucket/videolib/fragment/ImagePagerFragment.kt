package justbucket.videolib.fragment

import android.arch.lifecycle.ViewModelProviders
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.SharedElementCallback
import android.support.v4.view.ViewPager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.TransitionInflater
import android.view.View
import justbucket.videolib.MainActivity
import justbucket.videolib.R
import justbucket.videolib.adapter.ImagePagerAdapter
import justbucket.videolib.adapter.PortraitLayoutManager
import justbucket.videolib.adapter.TagListAdapter
import justbucket.videolib.di.InjectedFragment
import justbucket.videolib.model.VideoPres
import justbucket.videolib.viewmodel.BaseViewModel
import justbucket.videolib.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_imange_pager.*

class ImagePagerFragment : InjectedFragment<List<String>>() {

    private lateinit var videoList: List<VideoPres>
    private lateinit var adapter: TagListAdapter

    override val layoutId: Int
        get() = R.layout.fragment_imange_pager

    override val viewModel: BaseViewModel<List<String>>
        get() = ViewModelProviders.of(this, viewModelFactory)[DetailViewModel::class.java]

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        videoList = arguments?.getParcelableArrayList(VIDEO_LIST_KEY) ?: return

        pager_preview.adapter = ImagePagerAdapter(this, videoList)
        text_title.text = videoList[MainActivity.currentPosition].title
        // Set the current position and add a listener that will update the selection coordinator when
        // paging the images.
        pager_preview.currentItem = MainActivity.currentPosition
        pager_preview.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                MainActivity.currentPosition = position
                text_title.text = videoList[position].title
                adapter.parseSelected(videoList[position].tags)

            }
        })

        recycler_detail.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        if (view.context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recycler_detail.layoutManager = LinearLayoutManager(context)
        } else {
            recycler_detail.layoutManager = PortraitLayoutManager()
        }

        prepareSharedElementTransition()

        // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
    }

    override fun setupForError(message: String?) {
        text_empty_tags.visibility = View.VISIBLE
        recycler_detail.visibility = View.GONE
        text_empty_tags.text = message
    }

    override fun setupForSuccess(data: List<String>?) {
        if (data != null) {
            adapter = TagListAdapter(data,
                    object : TagListAdapter.TagHolderListener {
                        override fun onTagCheckChange(tag: String, checked: Boolean) {
                            val videoPres = videoList[MainActivity.currentPosition]
                            if (checked) videoPres.tags.add(tag) else videoPres.tags.remove(tag)
                            (viewModel as DetailViewModel).saveVideoTags(videoPres)
                        }
                    })
            adapter.parseSelected(videoList[MainActivity.currentPosition].tags)
            recycler_detail.adapter = adapter
            recycler_detail.visibility = View.VISIBLE
        } else setupForError(getString(R.string.no_tags))
    }

    override fun setupForLoading() {
        text_empty_tags.visibility = View.GONE
    }

    private fun prepareSharedElementTransition() {
        val transition = TransitionInflater.from(context)
                .inflateTransition(R.transition.image_shared_element_transition)
        sharedElementEnterTransition = transition

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(
                object : SharedElementCallback() {
                    override fun onMapSharedElements(names: List<String>?, sharedElements: MutableMap<String, View>?) {
                        // Locate the image view at the primary fragment (the ImageFragment that is currently
                        // visible). To locate the fragment, call instantiateItem with the selection position.
                        // At this stage, the method will simply return the fragment at the position and will
                        // not create a new one.
                        val currentFragment = pager_preview.adapter!!
                                .instantiateItem(pager_preview, MainActivity.currentPosition) as Fragment
                        val view = currentFragment.view ?: return

                        // Map the first shared element name to the child ImageView.
                        val sharedView = view.findViewById<View>(R.id.pager_image)
                        sharedElements!![names!![0]] = sharedView
                    }
                })
    }

    companion object {

        private const val VIDEO_LIST_KEY = "video=list-key"

        fun newInstance(videoList: List<VideoPres>): ImagePagerFragment {
            return ImagePagerFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(VIDEO_LIST_KEY, ArrayList(videoList))
                }
            }
        }
    }
}
