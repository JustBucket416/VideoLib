package justbucket.videolib.mapper

import justbucket.videolib.domain.model.Video
import justbucket.videolib.model.VideoPres
import javax.inject.Inject

class VideoMapper @Inject constructor() : Mapper<VideoPres, Video> {

    override fun mapToDomain(presentation: VideoPres): Video {
        return Video(presentation.id, presentation.title, presentation.videoPath,
                presentation.thumbPath, presentation.source, presentation.tags)
    }

    override fun mapToPresentation(domain: Video): VideoPres {
        return VideoPres(domain.id, domain.title, domain.videoPath,
                domain.thumbPath, domain.source, domain.tags.toMutableList())
    }
}
