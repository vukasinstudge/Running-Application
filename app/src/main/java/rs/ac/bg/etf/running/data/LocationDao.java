package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert
    long insert(Location location);

    @Query("SELECT * FROM Location WHERE workoutId = :workoutId AND username = :username")
    List<Location> getAll(int workoutId, String username);

    @Query("SELECT * FROM Location WHERE workoutId = :workoutId AND username = :username")
    LiveData<List<Location>> getAllLiveData(int workoutId, String username);
}
