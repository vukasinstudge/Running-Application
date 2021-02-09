package rs.ac.bg.etf.running.workouts;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutStartBinding;

@AndroidEntryPoint
public class WorkoutStartFragment extends Fragment {

    private static final String SHARED_PREFERENCES_NAME = "workout-shared-preferences";
    private static final String START_TIMESTAMP_KEY = "start-timestamp-key";

    private FragmentWorkoutStartBinding binding;
    private WorkoutViewModel workoutViewModel;
    private NavController navController;
    private MainActivity mainActivity;

    private Timer timer;
    private Timer timerRemaining;
    private SharedPreferences sharedPreferences;

    private LocationViewModel locationViewModel;

    static int indexSongs;
    private boolean paused = false;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isPermissionGranted -> {
                        if (isPermissionGranted) {
                            startWorkout(new Date().getTime());
                        }
                    });

    public WorkoutStartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
        locationViewModel = new ViewModelProvider(mainActivity).get(LocationViewModel.class);

        sharedPreferences = mainActivity
                .getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutStartBinding.inflate(inflater, container, false);

        timer = new Timer();
        timerRemaining = new Timer();

        if (sharedPreferences.contains(START_TIMESTAMP_KEY)) {
            startWorkout(sharedPreferences.getLong(START_TIMESTAMP_KEY, new Date().getTime()));
        }

        binding.start.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(
                    mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                if (MainActivity.getCurrPlaylist() == null) {
                    Toast.makeText(mainActivity, "Pick a playlist!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ActivityCompat.checkSelfPermission(mainActivity,
                        Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
                    requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
                } else {
                    startWorkout(new Date().getTime());
                }
            }
        });
        binding.finish.setOnClickListener(view -> finishWorkout());
        binding.cancel.setOnClickListener(view -> cancelWorkout());

        binding.pause.setOnClickListener(view -> {
            MediaPlayer mediaPlayer = LifecycleAwarePlayer.getStaticMediaPlayer();
            paused = true;
            mediaPlayer.pause();
        });

        binding.resume.setOnClickListener(view -> {
            MediaPlayer mediaPlayer = LifecycleAwarePlayer.getStaticMediaPlayer();
            paused = false;
            mediaPlayer.start();
        });

        binding.next.setOnClickListener(view -> {
            MediaPlayer mediaPlayer = LifecycleAwarePlayer.getStaticMediaPlayer();

            String songs = MainActivity.getCurrPlaylist().getSongs();
            indexSongs = WorkoutService.getIndexSongs();
            if (indexSongs >= songs.length()) {
                Toast.makeText(mainActivity, "This is the last song!", Toast.LENGTH_SHORT).show();
                return;
            }
            WorkoutService.setIndexSongs(indexSongs + 1);

            mediaPlayer.stop();
            mediaPlayer.reset();
            String song = "";
            int num = songs.charAt(indexSongs) - '0';
            int index = 0;
            for (String strFile : mainActivity.getFilesDir().list()) {
                if (num == index) song = strFile;
                index++;
            }
            String path = mainActivity.getFilesDir().getAbsolutePath() + File.separator + song;
            try {
                mediaPlayer.setDataSource(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnPreparedListener(mp -> {
                int duration = mediaPlayer.getDuration();
                mediaPlayer.start();

                int seconds = (int) ((duration / 1000) % 60);
                int minutes = (int) ((duration / (1000 * 60)) % 60);

                StringBuilder remaining = new StringBuilder();
                remaining.append(String.format("%02d", minutes)).append(":");
                remaining.append(String.format("%02d", seconds));

                TextView tw = (TextView) MainActivity.getStaticMain().findViewById(R.id.remaining);
                tw.setText(remaining);
            });
            mediaPlayer.prepareAsync();

            TextView tw = (TextView) MainActivity.getStaticMain().findViewById(R.id.playlist_name);
            tw.setText(MainActivity.getStaticMain().getResources().getString(R.string.empty_playlist) + " " + MainActivity.getCurrPlaylist().getName());
            tw = (TextView) MainActivity.getStaticMain().findViewById(R.id.song_name);
            tw.setText(MainActivity.getStaticMain().getResources().getString(R.string.empty_song) + " " + song);
        });

        binding.previous.setOnClickListener(view -> {
            MediaPlayer mediaPlayer = LifecycleAwarePlayer.getStaticMediaPlayer();

            String songs = MainActivity.getCurrPlaylist().getSongs();
            indexSongs = WorkoutService.getIndexSongs();
            if (indexSongs <= 1) {
                Toast.makeText(mainActivity, "This is the first song!", Toast.LENGTH_SHORT).show();
                return;
            }
            WorkoutService.setIndexSongs(indexSongs - 1);

            indexSongs -= 2;

            mediaPlayer.stop();
            mediaPlayer.reset();
            String song = "";
            int num = songs.charAt(indexSongs) - '0';
            int index = 0;
            for (String strFile : mainActivity.getFilesDir().list()) {
                if (num == index) song = strFile;
                index++;
            }
            String path = mainActivity.getFilesDir().getAbsolutePath() + File.separator + song;
            try {
                mediaPlayer.setDataSource(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnPreparedListener(mp -> {
                int duration = mediaPlayer.getDuration();
                mediaPlayer.start();

                int seconds = (int) ((duration / 1000) % 60);
                int minutes = (int) ((duration / (1000 * 60)) % 60);

                StringBuilder remaining = new StringBuilder();
                remaining.append(String.format("%02d", minutes)).append(":");
                remaining.append(String.format("%02d", seconds));

                TextView tw = (TextView) MainActivity.getStaticMain().findViewById(R.id.remaining);
                tw.setText(remaining);
            });
            mediaPlayer.prepareAsync();

            TextView tw = (TextView) MainActivity.getStaticMain().findViewById(R.id.playlist_name);
            tw.setText(MainActivity.getStaticMain().getResources().getString(R.string.empty_playlist) + " " + MainActivity.getCurrPlaylist().getName());
            tw = (TextView) MainActivity.getStaticMain().findViewById(R.id.song_name);
            tw.setText(MainActivity.getStaticMain().getResources().getString(R.string.empty_song) + " " + song);
        });

        mainActivity.getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        stopWorkout();
                    }
                });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer.cancel();
        timerRemaining.cancel();
    }

    private void startWorkout(long startTimestamp) {
        binding.start.setEnabled(false);
        binding.finish.setEnabled(true);
        binding.cancel.setEnabled(true);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(START_TIMESTAMP_KEY, startTimestamp);
        editor.commit();

        Handler handler = new Handler(Looper.getMainLooper());

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                long elapsed = new Date().getTime() - startTimestamp;

                int miliseconds = (int) ((elapsed % 1000) / 10);
                int seconds = (int) ((elapsed / 1000) % 60);
                int minutes = (int) ((elapsed / (1000 * 60)) % 60);
                int hours = (int) ((elapsed / (1000 * 60 * 60)) % 24);

                StringBuilder workoutDuration = new StringBuilder();
                workoutDuration.append(String.format("%02d", hours)).append(":");
                workoutDuration.append(String.format("%02d", minutes)).append(":");
                workoutDuration.append(String.format("%02d", seconds)).append(".");
                workoutDuration.append(String.format("%02d", miliseconds));

                handler.post(() -> binding.workoutDuration.setText(workoutDuration));
            }
        }, 0, 10);

        timerRemaining.schedule(new TimerTask() {
            @Override
            public void run() {

                String remainingString = binding.remaining.getText().toString();

                String[] units = remainingString.split(":");

                int remainingMinutes = Integer.parseInt(units[0]);
                int remainingSeconds = Integer.parseInt(units[1]);

                if (!(remainingMinutes == 0 && remainingSeconds == 0) && !paused) {
                    if (remainingSeconds == 0) {
                        remainingMinutes--;
                        remainingSeconds = 60;
                    }
                    remainingSeconds--;
                }

                StringBuilder remaining = new StringBuilder();
                remaining.append(String.format("%02d", remainingMinutes)).append(":");
                remaining.append(String.format("%02d", remainingSeconds));

                handler.post(() -> binding.remaining.setText(remaining));
            }
        }, 0, 1000);

        Intent intent = new Intent();
        intent.setClass(mainActivity, WorkoutService.class);
        intent.setAction(WorkoutService.INTENT_ACTION_START);
        mainActivity.startService(intent);
    }

    private void finishWorkout() {
        long startTimestamp = sharedPreferences.getLong(START_TIMESTAMP_KEY, new Date().getTime());
        long elapsed = new Date().getTime() - startTimestamp;
        double minutes = elapsed / (1000.0 * 60);
        TextView textView = (TextView) MainActivity.getStaticMain().findViewById(R.id.steps);
        String stepsString = textView.getText().toString();
        int numSteps = Integer.parseInt(stepsString.split(":")[1].substring(1));
        Workout newWorkout = new Workout(
                0,
                new Date(),
                getText(R.string.workout_label).toString(),
                0.2 * minutes,
                minutes,
                MainActivity.getCurrUsername(),
                numSteps
        );
        workoutViewModel.insertWorkout(newWorkout);

        long workoutId = 1;
        if (workoutViewModel.getEveryone().getValue() != null) {
            if (workoutViewModel.getEveryone().getValue().size() > 0) {
                int size = workoutViewModel.getEveryone().getValue().size();
                workoutId = workoutViewModel.getEveryone().getValue().get(size - 1).getId() + 1;
            }
        }
        for (int i = 0; i < MainActivity.getLatitude().size(); i++) {
            locationViewModel.insertLocation(new Location(
                    0,
                    workoutId,
                    MainActivity.getLatitude().get(i),
                    MainActivity.getLongitude().get(i),
                    MainActivity.getCurrUsername()
            ));
        }
        MainActivity.setLatitude(new ArrayList<>());
        MainActivity.setLongitude(new ArrayList<>());

        stopWorkout();
    }

    private void cancelWorkout() {
        stopWorkout();
    }

    private void stopWorkout() {
        Intent intent = new Intent();
        intent.setClass(mainActivity, WorkoutService.class);
        mainActivity.stopService(intent);
        sharedPreferences.edit().remove(START_TIMESTAMP_KEY).commit();
        navController.navigateUp();
    }
}
