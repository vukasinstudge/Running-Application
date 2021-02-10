package rs.ac.bg.etf.running.alarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.databinding.FragmentAlarmBinding;
import rs.ac.bg.etf.running.databinding.FragmentPlaylistCreateBinding;
import rs.ac.bg.etf.running.login.LoginViewModel;
import rs.ac.bg.etf.running.music.PlaylistViewModel;
import rs.ac.bg.etf.running.workouts.WorkoutService;

@AndroidEntryPoint
public class AlarmFragment extends Fragment {

    private FragmentAlarmBinding binding;
    private NavController navController;
    private MainActivity mainActivity;

    private int weekdayCalendar[] = {
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY
    };

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAlarmBinding.inflate(inflater, container, false);

        String[] weekdays = getResources().getStringArray(R.array.days_of_week);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                mainActivity,
                android.R.layout.simple_list_item_1,
                weekdays);
        binding.spinnerDays.setAdapter(arrayAdapter);

        binding.create.setOnClickListener(view -> {
            Intent intent = new Intent(mainActivity, ReminderBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, 0, intent, 0);

            Intent intentLocation = new Intent();
            intentLocation.setClass(mainActivity, WorkoutService.class);
            intentLocation.setAction(WorkoutService.INTENT_ACTION_LOCATION);
            mainActivity.startService(intentLocation);

            AlarmManager alarmManager = (AlarmManager) mainActivity.getSystemService(mainActivity.ALARM_SERVICE);

            String[] daysOfWeekStrings = getResources().getStringArray(R.array.days_of_week);
            int[] daysOfWeekValues = getResources().getIntArray(R.array.days_of_week_values);
            int weekdayValue = daysOfWeekValues[binding.spinnerDays.getSelectedItemPosition()];

            int hour = binding.timePicker.getHour();
            int minute = binding.timePicker.getMinute();
            int weekday = weekdayCalendar[weekdayValue];

            Calendar alarmCalendar = Calendar.getInstance();

            alarmCalendar.set(Calendar.DAY_OF_WEEK, weekday);
            alarmCalendar.set(Calendar.HOUR, hour);
            alarmCalendar.set(Calendar.MINUTE, minute);

            Long alarmTime = alarmCalendar.getTimeInMillis();

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 7 * AlarmManager.INTERVAL_DAY, pendingIntent);

            Toast.makeText(mainActivity,
                    "You set an alarm for " + hour + ":" + minute + " on every " + daysOfWeekStrings[binding.spinnerDays.getSelectedItemPosition()],
                    Toast.LENGTH_SHORT).show();

        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

}