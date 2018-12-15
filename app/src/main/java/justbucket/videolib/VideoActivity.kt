package justbucket.videolib

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import android.widget.VideoView
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import justbucket.videolib.model.VideoPres
import justbucket.videolib.service.MediaPlayerService
import kotlinx.android.synthetic.main.activity_video.*


class VideoActivity : AppCompatActivity() {

    companion object {
        /**
         * The number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        const val AUTO_HIDE_DELAY_MILLIS = 3000
        const val MANUAL_HIDE_DELAY_MILLIS = 100

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
        private const val LINKS_KEY = "links-key"

        fun newIntent(context: Context?, videos: ArrayList<VideoPres>): Intent {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putParcelableArrayListExtra(LINKS_KEY, videos)
            return intent
        }
    }

    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            val totalDuration = fullscreen_video.duration.toLong()
            val currentDuration = fullscreen_video.currentPosition.toLong()

            // Displaying Total Duration time
            text_view_total.text = String.format("%s", Utilities.milliSecondsToTimer(totalDuration))
            // Displaying time completed playing
            text_view_current.text = String.format("%s", Utilities.milliSecondsToTimer(currentDuration))

            // Updating progress bar
            val progress = Utilities.getProgressPercentage(currentDuration, totalDuration)
            seek_bar.progress = progress

            // Running this thread after 100 milliseconds
            mHideHandler.postDelayed(this, 100)
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mediaInterface = IMediaAidlInterface.Stub.asInterface(service)

            val link = videoList[MainActivity.currentPosition]
            playVideo(link)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mediaInterface = null
        }
    }

    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        fullscreen_video.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreen_content_controls.visibility = View.VISIBLE
    }
    private val mHideRunnable = Runnable { hide() }
    private val mHideHandler = Handler()
    private lateinit var videoList: ArrayList<VideoPres>
    private var mVisible: Boolean = false
    private var play = true
    private var mediaInterface: IMediaAidlInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        setContentView(R.layout.activity_video)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        videoList = intent.getParcelableArrayListExtra<VideoPres>(LINKS_KEY)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(MANUAL_HIDE_DELAY_MILLIS)
    }

    override fun onStart() {
        super.onStart()
        mVisible = true

        // Set up the user interaction to manually show or hide the system UI.
        fullscreen_video.setOnClickListener { toggle() }

        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // remove message Handler from updating progress bar
                mHideHandler.removeCallbacks(mUpdateTimeTask)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (fullscreen_video.isPlaying || fullscreen_video.currentPosition > 1) {
                    mHideHandler.removeCallbacks(mUpdateTimeTask)
                    val totalDuration = fullscreen_video.duration
                    val currentPosition = Utilities.progressToTimer(seekBar.progress, totalDuration)

                    // forward or backward to certain seconds
                    fullscreen_video.seekTo(currentPosition)
                    mediaInterface?.seekTo(currentPosition)

                    // update timer progress again
                    updateProgressBar()
                }
            }
        })

        image_button_play_pause.setOnClickListener {
            play = if (play) {
                fullscreen_video.seekTo(fullscreen_video.currentPosition)
                fullscreen_video.pause()
                mediaInterface?.changePlayState(false)
                image_button_play_pause.setImageResource(android.R.drawable.ic_media_play)
                false
            } else {
                fullscreen_video.start()
                mediaInterface?.changePlayState(true)
                image_button_play_pause.setImageResource(android.R.drawable.ic_media_pause)
                true
            }

            delayedHide(AUTO_HIDE_DELAY_MILLIS)

        }
        image_button_previous.setOnClickListener {
            if (MainActivity.currentPosition != 0)
                playVideo(videoList[--MainActivity.currentPosition])

            delayedHide(AUTO_HIDE_DELAY_MILLIS)

        }
        image_button_next.setOnClickListener {
            if (MainActivity.currentPosition != videoList.size - 1)
                playVideo(videoList[++MainActivity.currentPosition])
            else playVideo(videoList[0])

            delayedHide(AUTO_HIDE_DELAY_MILLIS)

        }

        fullscreen_video.setOnCompletionListener {
            if (MainActivity.currentPosition != videoList.size - 1) {
                playVideo(videoList[++MainActivity.currentPosition])
            } else {
                playVideo(videoList[0])
            }
        }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        player_layout.setOnClickListener {
            toggle()

            delayedHide(AUTO_HIDE_DELAY_MILLIS)

        }
    }

    override fun onResume() {
        super.onResume()

        bindService(MediaPlayerService.newBoundIntent(this),
                serviceConnection,
                Context.BIND_AUTO_CREATE)

        updateProgressBar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager.isInteractive) {
            unbindService(serviceConnection)
            mHideHandler.removeCallbacks(mUpdateTimeTask)
        }
    }

    /**
     * Plays the given video
     */
    @SuppressLint("StaticFieldLeak")
    private fun playVideo(video: VideoPres) {

        title = video.title

        when (video.source) {
            0 -> {
                fullscreen_video.setVideoPath(video.videoPath)
                fullscreen_video.setVolume(0)
                fullscreen_video.start()
                mediaInterface?.playAudio(video.videoPath)
            }
            1 -> {
                var orientation = requestedOrientation
                val rotation = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
                when (rotation) {
                    Surface.ROTATION_0 -> orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    Surface.ROTATION_90 -> orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    Surface.ROTATION_180 -> orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    Surface.ROTATION_270 -> orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                }
                requestedOrientation = orientation
                object : YouTubeExtractor(this) {
                    override fun onExtractionComplete(sparseArray: SparseArray<YtFile>?, videoMeta: VideoMeta?) {
                        if (sparseArray == null) {
                            Toast.makeText(this@VideoActivity, "Can't play video", Toast.LENGTH_SHORT).show()
                            finish()
                            return
                        }
                        var videoMax = 0
                        var audioMax = 0
                        for (i in 0 until sparseArray.size()) {
                            //if (sparseArray.keyAt(i) > videoMax && sparseArray.valueAt(i)?.format?.audioBitrate != -1 && sparseArray.valueAt(i)?.format?.height != -1) videoMax = sparseArray.keyAt(i)
                            if (sparseArray.valueAt(i)?.format?.height!! > videoMax) videoMax = sparseArray.keyAt(i)
                            if (sparseArray.valueAt(i)?.format?.audioBitrate!! > audioMax) audioMax = sparseArray.keyAt(i)
                        }
                        val videoUrl = sparseArray[videoMax].url
                        Log.i("youtube-video-url", videoUrl)
                        val audioUrl = sparseArray[audioMax].url
                        Log.i("youtube-audio-url", audioUrl)
                        fullscreen_video.setVideoPath(videoUrl)
                        fullscreen_video.setVolume(0)
                        fullscreen_video.setOnPreparedListener {
                            fullscreen_video.start()
                            mediaInterface?.playAudio(audioUrl)
                        }
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }
                }.extract(video.videoPath, true, true)
            }
        }
    }

    /**
     * Updates progressBar according to the video's progress
     */
    private fun updateProgressBar() {
        mHideHandler.postDelayed(mUpdateTimeTask, MANUAL_HIDE_DELAY_MILLIS.toLong())
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    /**
     * Hides the system and player UI
     */
    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreen_content_controls.visibility = View.GONE
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Shows the system and player UI
     */
    private fun show() {
        // Show the system bar
        fullscreen_video.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    fun VideoView.setVolume(amount: Int) {
        val max = 100
        val numerator: Double = if (max - amount > 0) Math.log((max - amount).toDouble()) else 0.0
        val volume = (1 - numerator / Math.log(max.toDouble())).toFloat()
        val field = this::class.java.getDeclaredField("mMediaPlayer")
        field.isAccessible = true
        (field.get(this) as MediaPlayer).setVolume(volume, volume)
        field.isAccessible = false
    }

}

