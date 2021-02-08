package rs.ac.bg.etf.running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.databinding.ActivityMainBinding;
import rs.ac.bg.etf.running.login.LoginViewModel;
import rs.ac.bg.etf.running.routes.RouteBrowseFragment;
import rs.ac.bg.etf.running.workouts.WorkoutListFragmentDirections;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "running-app-example";

    public static final String INTENT_ACTION_WORKOUT = "rs.ac.bg.etf.running.WORKOUT";

    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private NavDrawerUtil navDrawerUtil;
    private LoginViewModel loginViewModel;

    private static MainActivity staticMainActivity;

    private List<User> users;

    private static String currUsername = "";
    private static Playlist currPlaylist = null;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        MainActivity.setStaticMain(this);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        if (savedInstanceState == null) {
            setupNavDrawer();
        }

        loginViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                MainActivity.setCurrUsername(user.getUsername());
            }
        });

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
                R.navigation.navigation_playlists
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