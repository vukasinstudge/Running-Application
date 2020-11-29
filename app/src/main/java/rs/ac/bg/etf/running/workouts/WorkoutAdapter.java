package rs.ac.bg.etf.running.workouts;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import rs.ac.bg.etf.running.databinding.ViewHolderWorkoutBinding;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    public WorkoutAdapter() {

    }

    @NonNull
    @Override
    public WorkoutAdapter.WorkoutViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolderWorkoutBinding viewHolderWorkoutBinding = ViewHolderWorkoutBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new WorkoutAdapter.WorkoutViewHolder(viewHolderWorkoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutAdapter.WorkoutViewHolder holder, int position) {
        ViewHolderWorkoutBinding binding = holder.binding;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {

        public ViewHolderWorkoutBinding binding;

        public WorkoutViewHolder(@NonNull ViewHolderWorkoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
