package justbucket.videolib.data.mapper

interface Mapper<Domain, Data> {

    fun mapToDomain(data: Data): Domain

    fun mapToData(domain: Domain): Data
}