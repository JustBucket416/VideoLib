package justbucket.videolib.di.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import justbucket.videolib.di.ViewModelFactory
import justbucket.videolib.di.ViewModelKey
import justbucket.videolib.viewmodel.*

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(GridViewModel::class)
    abstract fun bindGridViewModel(viewModel: GridViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FilterViewModel::class)
    abstract fun bindNavBarViewModel(viewModel: FilterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ImportViewModel::class)
    abstract fun bindImportViewModel(viewModel: ImportViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    abstract fun bindDetailViewModel(viewModel: DetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ActionViewModel::class)
    abstract fun bindActionVIewModel(viewModel: ActionViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}