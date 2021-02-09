package rs.ac.bg.etf.running.workouts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutListBinding;

@AndroidEntryPoint
public class WorkoutListFragment extends Fragment {

    private FragmentWorkoutListBinding binding;
    private WorkoutViewModel workoutViewModel;
    private NavController navController;
    private MainActivity mainActivity;

    public static final String DIALOG_KEY = "dialog-key";

    public WorkoutListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutListBinding.inflate(inflater, container, false);

        binding.toolbar.inflateMenu(R.menu.menu_workout_list_options);
        binding.toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.workout_menu_item_sort:
                    workoutViewModel.invertSorted();
                    return true;
            }
            return false;
        });

        WorkoutAdapter workoutAdapter = new WorkoutAdapter();
        workoutViewModel.getWorkoutList().observe(
                getViewLifecycleOwner(),
                workoutAdapter::setWorkoutList);
        workoutViewModel.getEveryone().observe(
                getViewLifecycleOwner(),
                workoutAdapter::setEveryone
        );

        binding.recyclerView.setAdapter(workoutAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        binding.floatingActionButton.inflate(R.menu.menu_workout_list_fab);

        binding.floatingActionButton.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case R.id.workout_fab_create:
                    navController.navigate(WorkoutListFragmentDirections.createWorkout());
                    return false;
                case R.id.workout_fab_start:
                    navController.navigate(WorkoutListFragmentDirections.startWorkout());
                    return false;
                case R.id.workout_fab_filter_sort:
                    new FilterSortFragment().show(getChildFragmentManager(), null);

                    getChildFragmentManager().setFragmentResultListener(DIALOG_KEY, this,
                            (requestKey, result) -> {
                                String resultString = (String) result.getSerializable(FilterSortFragment.SET_FILTER_SORT_KEY);
                                String[] resultSplit = resultString.split("/");

                                double low = Double.parseDouble(resultSplit[0]);
                                double high = Double.parseDouble(resultSplit[1]);
                                boolean sort = false;
                                if (resultSplit[2].equals("1")) sort = true;

                                StringBuilder sb = new StringBuilder();
                                if (low == -1 || high == 1000000) sb.append("Filter off / ");
                                else sb.append("Filter on / ");
                                if (sort) sb.append("Sort on");
                                else sb.append("Sort off");

                                workoutViewModel.setFilter(low, high);

                                if (sort) workoutViewModel.sort();
                                else workoutViewModel.notSort();

                                binding.filterSortLabel.setText(sb.toString());
                            });
                    return false;
            }
            return true;
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }
}