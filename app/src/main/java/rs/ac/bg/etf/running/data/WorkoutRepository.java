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

    public List<Workout> getAll() {
        return workoutDao.getAll();
    }

    public LiveData<List<Workout>> getAllLiveData(double low, double high) {
        return workoutDao.getAllLiveData(low, high);
    }

    public LiveData<List<Workout>> getAllSortedLiveData(double low, double high) {
        return workoutDao.getAllSortedLiveData(low, high);
    }

}
