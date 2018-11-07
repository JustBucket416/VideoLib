package justbucket.videolib.data.mapper

interface Mapper<Domain, Data> {

    suspend fun mapToDomain(data: Data): Domain

    suspend fun mapToData(domain: Domain): Data
}