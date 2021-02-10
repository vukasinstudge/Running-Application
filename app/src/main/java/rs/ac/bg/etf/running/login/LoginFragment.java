package rs.ac.bg.etf.running.login;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.NavDrawerUtil;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.calories.CaloriesViewModel;
import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.databinding.FragmentCaloriesBinding;
import rs.ac.bg.etf.running.databinding.FragmentLoginBinding;
import rs.ac.bg.etf.running.music.PlaylistViewModel;
import rs.ac.bg.etf.running.workouts.WorkoutViewModel;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private NavController navController;
    private MainActivity mainActivity;
    private LoginViewModel loginViewModel;
    private PlaylistViewModel playlistViewModel;
    private WorkoutViewModel workoutViewModel;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        loginViewModel = new ViewModelProvider(mainActivity).get(LoginViewModel.class);
        playlistViewModel = new ViewModelProvider(mainActivity).get(PlaylistViewModel.class);
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        binding.loginButton.setOnClickListener(view -> {
            if (binding.username.getEditText().getText().toString().equals("")
                    || binding.password.getEditText().getText().toString().equals("")) {
                Toast.makeText(mainActivity, R.string.enter_username_password, Toast.LENGTH_SHORT).show();
                return;
            }
            List<User> userList = loginViewModel.getUserList().getValue();
            for (int i = 0; i < userList.size(); i++) {
                if (!binding.username.getEditText().getText().toString().equals(userList.get(i).getUsername())) continue;
                if (!binding.password.getEditText().getText().toString().equals(userList.get(i).getPassword())) continue;
                Log.d("pokusaj", userList.get(i).getEmail());
                Log.d("pokusaj", userList.get(i).getUsername());
                Log.d("pokusaj", userList.get(i).getPassword());
                loginViewModel.setCurrentUser(userList.get(i));

                playlistViewModel.refreshPlaylists();
                workoutViewModel.refreshWorkouts();

                boolean stayLoggedIn = binding.stayLoggedInCheckbox.isChecked();
                loginViewModel.updateUser(loginViewModel.getCurrentUser().getValue().getUsername(), stayLoggedIn);

                mainActivity.getNavDrawerUtil().changeNavHostFragment(R.id.nav_graph_routes);

                mainActivity.getDrawer().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                TextView usernameHeader = ((NavigationView) mainActivity.findViewById(R.id.nav_view)).getHeaderView(0)
                        .findViewById(R.id.username_header);
                TextView emailHeader = ((NavigationView) mainActivity.findViewById(R.id.nav_view)).getHeaderView(0)
                        .findViewById(R.id.email_header);

                usernameHeader.setText(loginViewModel.getCurrentUser().getValue().getUsername());
                emailHeader.setText(loginViewModel.getCurrentUser().getValue().getEmail());

                return;
            }
            Toast.makeText(mainActivity, R.string.invalid_login_data, Toast.LENGTH_SHORT).show();
        });

        binding.registerButton.setOnClickListener(view -> {
            List<User> userList = loginViewModel.getUserList().getValue();
            for (int i = 0; i < userList.size(); i++) {
                if (binding.usernameRegister.getEditText().getText().toString().equals(userList.get(i).getUsername())) {
                    Toast.makeText(mainActivity, R.string.username_exists, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (binding.email.getEditText().getText().toString().equals(userList.get(i).getEmail())) {
                    Toast.makeText(mainActivity, R.string.email_exists, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(mainActivity, "Registration succesful!", Toast.LENGTH_SHORT).show();
            loginViewModel.insertUser(new User(
                    binding.usernameRegister.getEditText().getText().toString(),
                    binding.email.getEditText().getText().toString(),
                    binding.passwordRegister.getEditText().getText().toString(),
                    false
            ));
        });

        loginViewModel.getUserList().observe(getViewLifecycleOwner(), users -> {
            for (int i = 0; i < users.size(); i++) {
                Log.d("pokusaj", users.get(i).toString());
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