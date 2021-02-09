package rs.ac.bg.etf.running.workouts;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.data.WorkoutRepository;

public class WorkoutViewModel extends ViewModel {

    private final WorkoutRepository workoutRepository;
    private final SavedStateHandle savedStateHandle;

    private static final String SORTED_KEY = "sorted-key";
    private boolean sorted = false;

    private double low = -1;
    private double high = 1000000;

    private final LiveData<List<Workout>> workouts;
    private final LiveData<List<Workout>> everyone;

    @ViewModelInject
    public WorkoutViewModel(
            WorkoutRepository workoutRepository,
            @Assisted SavedStateHandle savedStateHandle) {
        this.workoutRepository = workoutRepository;
        this.savedStateHandle = savedStateHandle;

        everyone = Transformations.switchMap(
                savedStateHandle.getLiveData(SORTED_KEY, false),
                sorted -> {
                    return workoutRepository.getEveryoneLiveData();
                }
        );

        workouts = Transformations.switchMap(
                savedStateHandle.getLiveData(SORTED_KEY, false),
                sorted -> {
                    if (!sorted) {
                        return workoutRepository.getAllLiveData(low, high, MainActivity.getCurrUsername());
                    } else {
                        return workoutRepository.getAllSortedLiveData(low, high, MainActivity.getCurrUsername());
                    }
                }
        );
    }

    public void refreshWorkouts() {
        savedStateHandle.set(SORTED_KEY, sorted);
    }

    public void setFilter(double low, double high) {
        this.low = low;
        this.high = high;
    }

    public void sort() {
        savedStateHandle.set(SORTED_KEY, sorted = true);
    }

    public void notSort() {
        savedStateHandle.set(SORTED_KEY, sorted = false);
    }

    public void invertSorted() {
        savedStateHandle.set(SORTED_KEY, sorted = !sorted);
    }

    public void insertWorkout(Workout workout) { workoutRepository.insert(workout); }

    public LiveData<List<Workout>> getWorkoutList() {
        return workouts;
    }

    public LiveData<List<Workout>> getEveryone() { return everyone; }

}
