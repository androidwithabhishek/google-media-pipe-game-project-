package gupta.abhishek.googlemediapipe.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gupta.abhishek.googlemediapipe.data.local.AppSettingsDao
import gupta.abhishek.googlemediapipe.data.local.GameDatabase
import gupta.abhishek.googlemediapipe.data.local.GameScoreDao
import gupta.abhishek.googlemediapipe.data.local.MIGRATION_1_2
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGameDatabase(@ApplicationContext context: Context): GameDatabase =
        Room.databaseBuilder(context, GameDatabase::class.java, "game.db")
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideGameScoreDao(database: GameDatabase): GameScoreDao =
        database.gameScoreDao()

    @Provides
    fun provideAppSettingsDao(database: GameDatabase): AppSettingsDao =
        database.appSettingsDao()
}
