package gupta.abhishek.googlemediapipe.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import gupta.abhishek.googlemediapipe.data.repository.ScoreRepositoryImpl
import gupta.abhishek.googlemediapipe.data.repository.SettingsRepositoryImpl
import gupta.abhishek.googlemediapipe.domain.repository.ScoreRepository
import gupta.abhishek.googlemediapipe.domain.repository.SettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindScoreRepository(impl: ScoreRepositoryImpl): ScoreRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
