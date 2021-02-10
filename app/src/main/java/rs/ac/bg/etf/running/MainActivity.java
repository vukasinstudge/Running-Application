package rs.ac.bg.etf.running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.databinding.ActivityMainBinding;
import rs.ac.bg.etf.running.login.LoginViewModel;
import rs.ac.bg.etf.running.routes.RouteBrowseFragment;
import rs.ac.bg.etf.running.workouts.LocationViewModel;
import rs.ac.bg.etf.running.workouts.WorkoutListFragmentDirections;
import rs.ac.bg.etf.running.workouts.WorkoutViewModel;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "running-app-example";

    public static final String INTENT_ACTION_WORKOUT = "rs.ac.bg.etf.running.WORKOUT";

    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private NavDrawerUtil navDrawerUtil;
    private LoginViewModel loginViewModel;
    private WorkoutViewModel workoutViewModel;
    private LocationViewModel locationViewModel;

    private static String currentWeatherModel = "";

    private static ArrayList<Double> latitude = new ArrayList<>();
    private static ArrayList<Double> longitude = new ArrayList<>();

    public static ArrayList<Double> getLatitude() {
        return latitude;
    }

    public static void setLatitude(ArrayList<Double> latitude) {
        MainActivity.latitude = latitude;
    }

    public static ArrayList<Double> getLongitude() {
        return longitude;
    }

    public static void setLongitude(ArrayList<Double> longitude) {
        MainActivity.longitude = longitude;
    }

    private static MainActivity staticMainActivity;

    private List<User> users;

    private static String currUsername = "";
    private static Playlist currPlaylist = null;

    public static int musicPlaying = 0;

    public static int getMusicPlaying() {
        return musicPlaying;
    }

    public static void setMusicPlaying(int musicPlaying) {
        MainActivity.musicPlaying = musicPlaying;
    }

    public static Playlist getCurrPlaylist() {
        return currPlaylist;
    }

    public static void setCurrPlaylist(Playlist currPlaylist) {
        MainActivity.currPlaylist = currPlaylist;
    }

    public static void setStaticMain(MainActivity mainActivity) {
        staticMainActivity = mainActivity;
    }

    public static MainActivity getStaticMain() {
        return staticMainActivity;
    }

    public List<User> getUsers() {
        return users;
    }

    public static long workoutId = 0;

    public static long getWorkoutId() {
        return workoutId;
    }

    public static void setWorkoutId(long workoutId) {
        MainActivity.workoutId = workoutId;
    }

    public static String getCurrentWeatherModel() {
        return currentWeatherModel;
    }

    public static void setCurrentWeatherModel(String currentWeatherModel) {
        MainActivity.currentWeatherModel = currentWeatherModel;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "AlarmChannel";
            String description = "Channel for alarm";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyAlarm",
                    name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createNotificationChannel();
        MainActivity.setStaticMain(this);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        workoutViewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        if (savedInstanceState == null) {
            setupNavDrawer();
        }

        loginViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                MainActivity.setCurrUsername(user.getUsername());
            }
        });


        /*workoutViewModel.getWorkoutList().observe(this, workouts -> {
            int size = workouts.size();
            long workoutId = workouts.size() - 1;
            for (int i = 0; i < MainActivity.getLatitude().size(); i++) {
                locationViewModel.insertLocation(new Location(
                        0,
                        workoutId,
                        MainActivity.getLatitude().get(i),
                        MainActivity.getLongitude().get(i)
                ));
            }
        });*/


        if (getIntent().getAction().equals(INTENT_ACTION_WORKOUT)) {
            NavController navController = navDrawerUtil
                    .changeNavHostFragment(R.id.nav_graph_workouts);
            if (navController != null) {
                navController.navigate(WorkoutListFragmentDirections.startWorkout());
            }
        }
    }

    @Override
    public void onBackPressed() {
        workoutViewModel.notSort();
        workoutViewModel.setFilter(-1, 1000000);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setupNavDrawer();
    }

    private void setupNavDrawer() {

        navDrawerUtil = new NavDrawerUtil(this);

        int[] navResourceIds = new int[]{
                R.navigation.navigation_login,
                R.navigation.navigation_routes,
                R.navigation.navigation_workouts,
                R.navigation.navigation_calories,
                R.navigation.navigation_playlists,
                R.navigation.navigation_alarm
        };
        navDrawerUtil.setup(
                binding.navView,
                getSupportFragmentManager(),
                navResourceIds,
                R.id.nav_host_container
        );

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, null,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public DrawerLayout getDrawer() {
        return drawer;
    }

    public ActivityMainBinding getBinding() {
        return binding;
    }

    public NavDrawerUtil getNavDrawerUtil() {
        return navDrawerUtil;
    }

    public static void setCurrUsername(String username) {
        currUsername = username;
    }

    public static String getCurrUsername() {
        return currUsername;
    }
}