package justbucket.videolib.service

import android.content.Context
import android.content.Intent
import android.widget.Toast
import dagger.android.DaggerService
import justbucket.videolib.R
import justbucket.videolib.domain.feature.video.AddVideo
import justbucket.videolib.mapper.mapToDomain
import justbucket.videolib.model.TagPres
import javax.inject.Inject

class ImportService : DaggerService() {

    @Inject
    lateinit var addVideo: AddVideo

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val paths: ArrayList<String> = intent.getStringArrayListExtra(LINKS_KEY)
        val tags: ArrayList<TagPres> = intent.getParcelableArrayListExtra<TagPres>(TAGS_KEY)
        Toast.makeText(this, getString(R.string.string_adding), Toast.LENGTH_SHORT).show()
        paths.forEach { path ->
            addVideo.execute({ either ->
                either.either({
                    Toast.makeText(this, getString(R.string.import_video_error), Toast.LENGTH_SHORT).show()
                    stopSelf()
                }
                ) {
                    if (it) Toast.makeText(this, getString(R.string.string_import_success), Toast.LENGTH_SHORT).show()
                    else Toast.makeText(this, getString(R.string.string_import_stored), Toast.LENGTH_SHORT).show()
                    stopSelf()
                }
            }, AddVideo.Params.createParams(path, tags.map { it.mapToDomain() }))
        }
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        private const val LINKS_KEY = "link-key"
        private const val TAGS_KEY = "tags-key"

        fun newIntent(context: Context, links: List<String>, tags: ArrayList<TagPres>): Intent {
            return Intent(context, ImportService::class.java).apply {
                putStringArrayListExtra(LINKS_KEY, ArrayList(links))
                putParcelableArrayListExtra(TAGS_KEY, tags)
            }
        }
    }
}
