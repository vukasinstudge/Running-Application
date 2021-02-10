package rs.ac.bg.etf.running.workouts;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.annotation.Nullable;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutStatisticBinding;

public class WorkoutStatisticFragment extends Fragment {

    private FragmentWorkoutStatisticBinding binding;
    private NavController navController;
    private MainActivity mainActivity;
    private WorkoutViewModel workoutViewModel;

    private List<Workout> workouts;

    public WorkoutStatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);

        workoutViewModel.getWorkoutList().observe(mainActivity, workoutList -> {
            workouts = workoutList;
        });
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutStatisticBinding.inflate(inflater, container, false);

        binding.toolbar.setNavigationOnClickListener(view -> {
            navController.navigateUp();
        });

        workoutViewModel.getWorkoutList().observe(mainActivity, workoutList -> {
            workouts = workoutList;
        });

        int workoutsNum = 0;
        int stepsNum = 0;
        double totalDuration = 0;
        for (int i = 0; i < workouts.size(); i++) {
            workoutsNum++;
            stepsNum += workouts.get(i).getNumSteps();
            totalDuration += workouts.get(i).getDuration();
        }
        double averageDuration = totalDuration / workoutsNum;

        double longestDistance = 0;
        for (int i = 0; i < workouts.size(); i++) {
            if (workouts.get(i).getDuration() > longestDistance) {
                longestDistance = workouts.get(i).getDuration();
            }
        }

        binding.numWorkouts.setText(getString(R.string.number_of_workouts_prefix) + " " + workoutsNum);
        binding.totalSteps.setText(getString(R.string.total_steps_prefix) + " " + stepsNum);
        binding.longestDistance.setText(getString(R.string.longest_distance_prefix) + " " + String.format("%.2f km", longestDistance));
        binding.averageDuration.setText(getString(R.string.average_duration_prefix) + " " + String.format("%s min", DateTimeUtil.realMinutesToString(averageDuration)));

        int colorSuccess = Color.GREEN;
        int colorNotYey = Color.RED;

        if (stepsNum >= 100) binding.totalStepsBeginner.setTextColor(colorSuccess);
        else binding.totalStepsBeginner.setTextColor(colorNotYey);
        if (stepsNum >= 1000) binding.totalStepsMedium.setTextColor(colorSuccess);
        else binding.totalStepsMedium.setTextColor(colorNotYey);
        if (stepsNum >= 20000) binding.totalStepsHard.setTextColor(colorSuccess);
        else binding.totalStepsHard.setTextColor(colorNotYey);
        if (stepsNum >= 200000) binding.totalStepsExpert.setTextColor(colorSuccess);
        else binding.totalStepsExpert.setTextColor(colorNotYey);

        if (workoutsNum >= 1) binding.workoutNumberBeginner.setTextColor(colorSuccess);
        else binding.workoutNumberBeginner.setTextColor(colorNotYey);
        if (workoutsNum >= 20) binding.workoutNumberMedium.setTextColor(colorSuccess);
        else binding.workoutNumberMedium.setTextColor(colorNotYey);
        if (workoutsNum >= 500) binding.workoutNumberHard.setTextColor(colorSuccess);
        else binding.workoutNumberHard.setTextColor(colorNotYey);
        if (workoutsNum >= 5000) binding.workoutNumberExpert.setTextColor(colorSuccess);
        else binding.workoutNumberExpert.setTextColor(colorNotYey);

        if (totalDuration >= 5) binding.totalDurationBeginner.setTextColor(colorSuccess);
        else binding.totalDurationBeginner.setTextColor(colorNotYey);
        if (totalDuration >= 50) binding.totalDurationMedium.setTextColor(colorSuccess);
        else binding.totalDurationMedium.setTextColor(colorNotYey);
        if (totalDuration >= 1000) binding.totalDurationHard.setTextColor(colorSuccess);
        else binding.totalDurationHard.setTextColor(colorNotYey);
        if (totalDuration >= 10000) binding.totalDurationExpert.setTextColor(colorSuccess);
        else binding.totalDurationExpert.setTextColor(colorNotYey);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }
}