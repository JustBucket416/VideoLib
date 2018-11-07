package justbucket.videolib.mapper

interface Mapper<Presentation, Domain> {

    fun mapToDomain(presentation: Presentation): Domain

    fun mapToPresentation(domain: Domain): Presentation
}
