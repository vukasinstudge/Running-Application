package rs.ac.bg.etf.running.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import rs.ac.bg.etf.running.data.LocationDao;
import rs.ac.bg.etf.running.data.PlaylistDao;
import rs.ac.bg.etf.running.data.RunDatabase;
import rs.ac.bg.etf.running.data.UserDao;
import rs.ac.bg.etf.running.data.WorkoutDao;

@Module
@InstallIn(SingletonComponent.class)
public interface DatabaseModule {

    @Provides
    static WorkoutDao provideWorkoutDao(@ApplicationContext Context context) {
        return RunDatabase.getInstance(context).workoutDao();
    }

    @Provides
    static UserDao provideUserDao(@ApplicationContext Context context) {
        return RunDatabase.getInstance(context).userDao();
    }

    @Provides
    static PlaylistDao providePlaylistDao(@ApplicationContext Context context) {
        return RunDatabase.getInstance(context).playlistDao();
    }

    @Provides
    static LocationDao provideLocationDao(@ApplicationContext Context context) {
        return RunDatabase.getInstance(context).locationDao();
    }

}
