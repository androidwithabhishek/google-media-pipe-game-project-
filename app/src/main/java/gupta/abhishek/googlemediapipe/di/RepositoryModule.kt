package gupta.abhishek.googlemediapipe.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import gupta.abhishek.googlemediapipe.data.repository.ScoreRepositoryImpl
import gupta.abhishek.googlemediapipe.domain.repository.ScoreRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindScoreRepository(impl: ScoreRepositoryImpl): ScoreRepository
}
