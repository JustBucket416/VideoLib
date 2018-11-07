package justbucket.videolib.di

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import justbucket.videolib.state.Resource
import justbucket.videolib.state.ResourceState
import justbucket.videolib.viewmodel.BaseViewModel
import javax.inject.Inject

abstract class InjectedDialogFragment<Data> : DialogFragment() {

    abstract val layoutId: Int
    abstract val viewModel: BaseViewModel<Data>

    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
        viewModel.getData().observe(context as LifecycleOwner, Observer { resource ->
            resource?.let {
                handleDataState(it)
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    private fun handleDataState(resource: Resource<Data>) {
        when (resource.status) {
            ResourceState.LOADING -> setupForLoading()
            ResourceState.SUCCESS -> setupForSuccess(resource.data)
            ResourceState.ERROR -> setupForError(resource.message)
        }
    }

    abstract fun setupForError(message: String?)

    abstract fun setupForSuccess(data: Data?)

    abstract fun setupForLoading()
}