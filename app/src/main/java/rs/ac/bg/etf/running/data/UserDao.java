package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User")
    LiveData<List<User>> getAllLiveData();

    @Query("UPDATE User SET stayLoggedIn = :stayLoggedIn WHERE username = :username")
    void updateUser(String username, boolean stayLoggedIn);

}
