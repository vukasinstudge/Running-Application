package rs.ac.bg.etf.running.workouts;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentFilterSortBinding;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutListBinding;

public class FilterSortFragment extends DialogFragment {

    private FragmentFilterSortBinding binding;
    public static final String SET_FILTER_SORT_KEY = "set-filter-sort-key";

    double low = -1;
    double high = -1;
    boolean sort = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        binding = FragmentFilterSortBinding.inflate(LayoutInflater.from(getContext()));

        binding.done.setOnClickListener(view -> {
            try {
                low = Double.parseDouble(binding.distanceLow.getEditText().getText().toString());
                high = Double.parseDouble(binding.distanceHigh.getEditText().getText().toString());
            } catch (NumberFormatException nfe) {
                low = -1;
                high = 1000000;
            }

            sort = binding.checkboxSort.isChecked();
            StringBuilder responseSB = new StringBuilder();
            responseSB.append(String.valueOf(low)).append('/').append(String.valueOf(high)).append('/');
            if (sort) responseSB.append(String.valueOf(1));
            else responseSB.append(String.valueOf(0));

            String response = responseSB.toString();

            Bundle result = new Bundle();
            result.putSerializable(SET_FILTER_SORT_KEY, response);
            getParentFragmentManager().setFragmentResult(WorkoutListFragment.DIALOG_KEY, result);

            this.dismiss();

        });

        return new AlertDialog.Builder(requireActivity())
                .setView(binding.getRoot())
                .create();
    }
}
