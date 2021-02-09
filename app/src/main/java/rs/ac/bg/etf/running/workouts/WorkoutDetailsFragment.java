package rs.ac.bg.etf.running.workouts;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.print.PrintAttributes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.FragmentRouteDetailsBinding;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutDetailsBinding;
import rs.ac.bg.etf.running.routes.Route;
import rs.ac.bg.etf.running.routes.RouteDetailsFragmentArgs;
import rs.ac.bg.etf.running.routes.RouteViewModel;

public class WorkoutDetailsFragment extends Fragment {

    private FragmentWorkoutDetailsBinding binding;
    private NavController navController;
    private MainActivity mainActivity;
    private WorkoutViewModel workoutViewModel;
    private LocationViewModel locationViewModel;

    List<Location> locations;

    private static long staticWorkoutId = 0;
    private static List<Location> staticLocations =  new ArrayList<>();

    public static long getStaticWorkoutId() {
        return staticWorkoutId;
    }

    public static void setStaticWorkoutId(long staticWorkoutId) {
        WorkoutDetailsFragment.staticWorkoutId = staticWorkoutId;
    }

    public static List<Location> getStaticLocations() {
        return staticLocations;
    }

    public static void setStaticLocations(List<Location> locations) {
        staticLocations = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getWorkoutId() == staticWorkoutId) {
                staticLocations.add(locations.get(i));
            }
        }
    }

    public WorkoutDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
        locationViewModel = new ViewModelProvider(mainActivity).get(LocationViewModel.class);

        locationViewModel.getLocations().observe(mainActivity, locationList -> {
            locations = locationList;
        });
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutDetailsBinding.inflate(inflater, container, false);

        Workout selectedWorkout = workoutViewModel.getWorkoutList().getValue().get(
                WorkoutDetailsFragmentArgs.fromBundle(requireArguments()).getWorkoutIndex());

        binding.toolbar.setNavigationOnClickListener(view -> {
            navController.navigateUp();
        });

        binding.workoutDateDetails.setText("Date: " + DateTimeUtil.getSimpleDateFormat().format(
                selectedWorkout.getDate()));
        binding.workoutLabelDetails.setText(
                selectedWorkout.getLabel());
        binding.workoutDistanceValueDetails.setText(String.format("%.2f km",
                selectedWorkout.getDistance()));
        binding.workoutPaceDetails.setText("Pace: " + String.format("%s min/km", DateTimeUtil.realMinutesToString(
                selectedWorkout.getDuration() / selectedWorkout.getDistance())));
        binding.workoutDurationValueDetails.setText(String.format("%s min", DateTimeUtil.realMinutesToString(
                selectedWorkout.getDuration())));
        binding.workoutStepsDetails.setText("Steps: " + selectedWorkout.getNumSteps());

        LinearLayout holder = binding.holder;

        MyCustomView myCustomView = new MyCustomView(mainActivity);

        myCustomView.setLayoutParams(new ViewGroup.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 450));
        myCustomView.setLayoutParams(new ViewGroup.MarginLayoutParams(0, 10));

        setStaticWorkoutId(selectedWorkout.getId());
        setStaticLocations(locations);

        holder.addView(myCustomView);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }
}