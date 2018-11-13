package justbucket.videolib.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.app.NotificationCompat
import android.view.KeyEvent
import com.bumptech.glide.RequestManager
import dagger.android.DaggerService
import justbucket.videolib.IMediaAidlInterface
import justbucket.videolib.model.VideoPres
import javax.inject.Inject

class MediaPlayerService : DaggerService() {

    @Inject
    lateinit var requestManager: RequestManager
    private val mp = MediaPlayer()
    private lateinit var audioLink: String
    private var millis = 0
    //private val eventReceiver = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager

    override fun onBind(intent: Intent): IBinder {
        return object : IMediaAidlInterface.Stub() {
            override fun changePlayState(play: Boolean) {
                if (play) {
                    mp.start()
                    mp.seekTo(millis)
                } else {
                    millis = mp.currentPosition
                    mp.stop()
                }
            }

            override fun playAudio(audio: String?) {
                if (mp.isPlaying) {
                    mp.reset()
                }
                mp.setDataSource(audio)
                mp.prepare()
                mp.start()
            }

            override fun seekTo(millis: Int) {
                mp.seekTo(millis)
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mp.stop()
        mp.release()
        return super.onUnbind(intent)
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, MEDIA_CHANNEL_NAME)
        builder.addAction(android.R.drawable.ic_media_previous, "previous",
                buildMediaButtonIntent(KeyEvent.KEYCODE_MEDIA_PREVIOUS))
    }

    private fun buildMediaButtonIntent(keycode: Int): PendingIntent {
        val intent = Intent(Intent.ACTION_MEDIA_BUTTON)
        //intent.component = eventReceiver
        intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_DOWN, keycode))
        return PendingIntent.getBroadcast(this, keycode, intent, 0)
    }

    companion object {

        private const val AUDIO_LIST_KEY = "audio-list-key"
        private const val POSITION_KEY = "position-key"
        private const val MEDIA_CHANNEL_NAME = "media-channel-name"

        fun newUnboundIntent(context: Context?, audioList: ArrayList<VideoPres>, position: Int): Intent {
            return Intent(context, MediaPlayerService::class.java).apply {
                putParcelableArrayListExtra(AUDIO_LIST_KEY, ArrayList(audioList.map { StringPair(it.videoPath, it.thumbPath) }))
                putExtra(POSITION_KEY, position)
            }
        }

        fun newBoundIntent(context: Context?): Intent {
            return Intent(context, MediaPlayerService::class.java).apply {
                action = IMediaAidlInterface::class.java.simpleName
            }
        }

        private data class StringPair(val first: String,
                                      val second: String) : Parcelable {
            constructor(parcel: Parcel) : this(
                    parcel.readString(),
                    parcel.readString())

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(first)
                parcel.writeString(second)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<StringPair> {
                override fun createFromParcel(parcel: Parcel): StringPair {
                    return StringPair(parcel)
                }

                override fun newArray(size: Int): Array<StringPair?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}
