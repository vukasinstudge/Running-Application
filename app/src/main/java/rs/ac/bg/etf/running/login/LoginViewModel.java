package rs.ac.bg.etf.running.login;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.data.UserRepository;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.data.WorkoutRepository;
import rs.ac.bg.etf.running.routes.Route;

public class LoginViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final SavedStateHandle savedStateHandle;

    private static final String USERS_KEY = "users-key";

    private final LiveData<List<User>> users;

    private MutableLiveData<User> currentUser = new MutableLiveData<>(null);

    @ViewModelInject
    public LoginViewModel(
            UserRepository userRepository,
            @Assisted SavedStateHandle savedStateHandle) {
        this.userRepository = userRepository;
        this.savedStateHandle = savedStateHandle;

        users = Transformations.switchMap(
                savedStateHandle.getLiveData(USERS_KEY, 0),
                users -> {
                    return userRepository.getAllLiveData();
                }
        );
    }

    public void insertUser(User user) {
        userRepository.insert(user);
        savedStateHandle.set(USERS_KEY, 1);
    }

    public void updateUser(String username, boolean stayLoggedIn) {
        userRepository.updateUser(username, stayLoggedIn);
        savedStateHandle.set(USERS_KEY, 1);
    }

    public void refreshUserList() {
        savedStateHandle.set(USERS_KEY, 1);
    }

    public LiveData<List<User>> getUserList() {
        return users;
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser.setValue(currentUser);
    }
}
