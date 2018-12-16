package justbucket.videolib.viewmodel

import android.graphics.Bitmap
import justbucket.videolib.domain.feature.ddsearch.SearchByImage
import justbucket.videolib.model.FilterPres
import justbucket.videolib.state.Resource
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class SecondViewModel @Inject constructor(private val searchByImage: SearchByImage)
    : BaseViewModel<ArrayList<String>>() {

    fun loadtags(bitmap: Bitmap) {
        liveData.postValue(Resource.loading())
        searchByImage.execute(onResult = { either ->
            either.either( {
                liveData.postValue(Resource.error(it.toString()))
            }, {
                liveData.postValue(Resource.success(it))
            })
        }, params = SearchByImage.Params.createParams(getBytesFromBitmap(bitmap)))
    }

    private fun getBytesFromBitmap(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        bitmap.recycle()
        return byteArray
    }

    fun saveFilterPres(filterPres: FilterPres) {

    }
}