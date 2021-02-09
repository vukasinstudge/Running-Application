package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class WorkoutRepository {

    private final ExecutorService executorService;

    private final WorkoutDao workoutDao;

    @Inject
    public WorkoutRepository(
            ExecutorService executorService,
            WorkoutDao workoutDao) {
        this.executorService = executorService;
        this.workoutDao = workoutDao;
    }

    public void insert(Workout workout) {
        executorService.submit(() -> workoutDao.insert(workout));
    }

    public List<Workout> getEveryone() {
        return workoutDao.getEveryone();
    }

    public LiveData<List<Workout>> getEveryoneLiveData() {
        return workoutDao.getEveryoneLiveData();
    }

    public List<Workout> getAll(String username) {
        return workoutDao.getAll(username);
    }

    public LiveData<List<Workout>> getAllLiveData(double low, double high, String username) {
        return workoutDao.getAllLiveData(low, high, username);
    }

    public LiveData<List<Workout>> getAllSortedLiveData(double low, double high, String username) {
        return workoutDao.getAllSortedLiveData(low, high, username);
    }

}
