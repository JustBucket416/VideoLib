package justbucket.videolib.state

/**
 * A domain data wrapper class
 */
class Resource<out T> private constructor(val status: ResourceState,
                                          val data: T?,
                                          val message: String?) {

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(ResourceState.SUCCESS, data, null)
        }

        fun <T> error(message: String?): Resource<T> {
            return Resource(ResourceState.ERROR, null, message)
        }

        fun <T> loading(): Resource<T> {
            return Resource(ResourceState.LOADING, null, null)
        }
    }

}