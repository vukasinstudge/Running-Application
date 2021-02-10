package rs.ac.bg.etf.running.music;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.FragmentPlaylistCreateBinding;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutCreateBinding;
import rs.ac.bg.etf.running.login.LoginViewModel;
import rs.ac.bg.etf.running.workouts.DatePickerFragment;
import rs.ac.bg.etf.running.workouts.DateTimeUtil;
import rs.ac.bg.etf.running.workouts.WorkoutViewModel;

@AndroidEntryPoint
public class PlaylistCreateFragment extends Fragment {

    private FragmentPlaylistCreateBinding binding;
    private PlaylistViewModel playlistViewModel;
    private NavController navController;
    private MainActivity mainActivity;
    private LoginViewModel loginViewModel;

    public PlaylistCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        playlistViewModel = new ViewModelProvider(mainActivity).get(PlaylistViewModel.class);
        loginViewModel = new ViewModelProvider(mainActivity).get(LoginViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlaylistCreateBinding.inflate(inflater, container, false);

        File dirFiles = mainActivity.getFilesDir();
        int i = 0;
        for (String strFile : dirFiles.list()) {
            TableRow row =new TableRow(mainActivity);
            row.setId(i);
            TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            tableRowParams.setMargins(0, 5, 0, 5);
            row.setLayoutParams(tableRowParams);
            CheckBox checkBox = new CheckBox(mainActivity);
            checkBox.setId(i);
            checkBox.setText(strFile);
            row.addView(checkBox);
            binding.checkboxHolder.addView(row);
            i++;
        }

        binding.toolbar.setNavigationOnClickListener(
                view -> navController.navigateUp());

        binding.create.setOnClickListener(view -> {

            String name = binding.playlistLabel.getEditText().getText().toString();
            String songs = "";

            File files = mainActivity.getFilesDir();
            int index = 0;
            StringBuilder sb = new StringBuilder();
            for (String strFile : files.list()) {
                TableRow row = (TableRow) binding.checkboxHolder.findViewById(index);
                CheckBox checkbox = (CheckBox) row.getChildAt(0);
                if (checkbox.isChecked()) sb.append(index).append('/');
                index++;
            }
            sb.deleteCharAt(sb.length() - 1);
            songs = sb.toString();

            if (!(name.equals("") || songs.equals(""))) {
                Playlist newPlaylist = new Playlist(
                        0,
                        name,
                        songs,
                        loginViewModel.getCurrentUser().getValue().getUsername()
                );
                playlistViewModel.insertPlaylist(newPlaylist);
                playlistViewModel.setCurrentPlaylist(newPlaylist);
                navController.navigateUp();
            } else {
                Toast.makeText(mainActivity, "Enter name and songs!", Toast.LENGTH_LONG).show();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

}