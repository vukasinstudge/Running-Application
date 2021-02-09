package rs.ac.bg.etf.running.workouts;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.data.LocationRepository;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.data.PlaylistRepository;

public class LocationViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final SavedStateHandle savedStateHandle;

    private static final String LOCATION_KEY = "location-key";

    private final LiveData<List<Location>> locations;

    @ViewModelInject
    public LocationViewModel(
            LocationRepository locationRepository,
            @Assisted SavedStateHandle savedStateHandle) {
        this.locationRepository = locationRepository;
        this.savedStateHandle = savedStateHandle;

        locations = Transformations.switchMap(
                savedStateHandle.getLiveData(LOCATION_KEY, 0),
                locations -> {
                    return locationRepository.getAllLiveData(MainActivity.getCurrUsername());
                }
        );
    }

    public void insertLocation(Location location) {
        locationRepository.insert(location);
        savedStateHandle.set(LOCATION_KEY, 1);
    }

    public void refreshLocations() {
        savedStateHandle.set(LOCATION_KEY, 1);
    }

    public LiveData<List<Location>> getLocations() {
        return locations;
    }

}
