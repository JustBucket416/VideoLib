package justbucket.videolib.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.widget.Toast
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import justbucket.videolib.R
import justbucket.videolib.model.VideoPres
import kotlinx.android.synthetic.main.fragment_image.*

class ImageFragment : Fragment() {

    private val handler = Handler()
    private val hideRunnable = Runnable { pager_icon_play.visibility = View.GONE }
    private var play: Boolean? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val videoPres = arguments?.getParcelable<VideoPres>(VIDEO_KEY) ?: return
        // Just like we do when binding views at the grid, we set the transition name to be the string
        // value of the image res.
        pager_image.transitionName = videoPres.id.toString()

        // Load the image with Glide to prevent OOM error when the image drawables are very large.
        Glide.with(this)
                .load(videoPres.thumbPath)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                        // startPostponedEnterTransition() should also be called on it to get the transition
                        // going in case of a failure.
                        parentFragment!!.startPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                        // startPostponedEnterTransition() should also be called on it to get the transition
                        // going when the image is ready.
                        parentFragment!!.startPostponedEnterTransition()
                        return false
                    }
                })
                .into(pager_image)

        //Setting listeners
        pager_icon_play.setOnClickListener {
            if (play == null) {
                play = true
                playVideo(videoPres)
                pager_icon_play.setImageResource(R.drawable.pause)
            } else {
                play = if (play!!) {
                    pager_video.pause()
                    pager_icon_play.setImageResource(R.drawable.play)
                    handler.postDelayed(hideRunnable, HIDE_DELAY_MILLIS)
                    false
                } else {
                    pager_video.seekTo(pager_video.currentPosition)
                    pager_video.start()
                    pager_icon_play.setImageResource(R.drawable.pause)
                    handler.postDelayed(hideRunnable, HIDE_DELAY_MILLIS)
                    true
                }
            }
        }

        pager_layout.setOnClickListener {
            pager_icon_play.visibility = if (pager_icon_play.visibility == View.GONE) View.VISIBLE else View.GONE
            handler.postDelayed(hideRunnable, HIDE_DELAY_MILLIS)
        }
    }

    override fun onResume() {
        super.onResume()
        if (play == null) pager_icon_play.visibility = View.VISIBLE
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (!isVisibleToUser) {
            pager_icon_play?.setImageResource(R.drawable.play)
            pager_icon_play?.visibility = View.VISIBLE
            pager_video?.stopPlayback()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(hideRunnable)
        pager_video.stopPlayback()
        pager_image.visibility = View.VISIBLE
        pager_video.visibility = View.GONE
    }

    /**
     * Plays video from given position
     *
     * @param video - a video to play
     * @param position - a timestamp position
     */
    @SuppressLint("StaticFieldLeak")
    private fun playVideo(video: VideoPres, position: Int? = null) {
        pager_image.visibility = View.GONE
        pager_icon_play.visibility = View.GONE
        pager_video.visibility = View.VISIBLE
        when (video.source) {
            0 -> {
                pager_video.setVideoPath(video.videoPath)
                if (position != null) pager_video.seekTo(position)
                pager_video.start()
            }
            1 -> {
                var orientation = activity?.requestedOrientation
                        ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                val rotation = (activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
                when (rotation) {
                    Surface.ROTATION_0 -> orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    Surface.ROTATION_90 -> orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    Surface.ROTATION_180 -> orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    Surface.ROTATION_270 -> orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                }
                activity?.requestedOrientation = orientation
                object : YouTubeExtractor(context!!) {
                    override fun onExtractionComplete(sparseArray: SparseArray<YtFile>?, videoMeta: VideoMeta?) {
                        if (sparseArray == null) {
                            Toast.makeText(this@ImageFragment.context, "Can't play video", Toast.LENGTH_SHORT).show()
                            return
                        }
                        var max = 0
                        for (i in 0 until sparseArray.size()) {
                            if (sparseArray.keyAt(i) > max && sparseArray.valueAt(i)?.format?.audioBitrate != -1 && sparseArray.valueAt(i)?.format?.height != -1) max = sparseArray.keyAt(i)
                        }
                        val url = sparseArray[max].url
                        Log.i("youtube-url", url)
                        pager_video.setVideoPath(url)

                        pager_video.start()
                        orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }
                }.extract(video.videoPath, false, false)
            }
        }
    }

    companion object {

        private const val VIDEO_KEY = "video-key"
        private const val HIDE_DELAY_MILLIS: Long = 1000

        fun newInstance(videoPres: VideoPres): ImageFragment {
            val imageFragment = ImageFragment()
            val args = Bundle()
            args.putParcelable(VIDEO_KEY, videoPres)
            imageFragment.arguments = args
            return imageFragment
        }
    }
}
