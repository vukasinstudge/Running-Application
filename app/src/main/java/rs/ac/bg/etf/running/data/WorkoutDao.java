package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WorkoutDao {

    @Insert
    long insert(Workout workout);

    @Query("SELECT * FROM Workout")
    List<Workout> getEveryone();

    @Query("SELECT * FROM Workout")
    LiveData<List<Workout>> getEveryoneLiveData();

    @Query("SELECT * FROM Workout WHERE username = :username")
    List<Workout> getAll(String username);

    @Query("SELECT * FROM Workout WHERE distance >= :low AND distance <= :high AND username = :username")
    LiveData<List<Workout>> getAllLiveData(double low, double high, String username);

    @Query("SELECT * FROM Workout WHERE distance >= :low AND distance <= :high AND username = :username ORDER BY distance DESC")
    LiveData<List<Workout>> getAllSortedLiveData(double low, double high, String username);
}
