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
    List<Workout> getAll();

    @Query("SELECT * FROM Workout WHERE distance >= :low AND distance <= :high")
    LiveData<List<Workout>> getAllLiveData(double low, double high);

    @Query("SELECT * FROM Workout WHERE distance >= :low AND distance <= :high ORDER BY distance DESC")
    LiveData<List<Workout>> getAllSortedLiveData(double low, double high);
}
