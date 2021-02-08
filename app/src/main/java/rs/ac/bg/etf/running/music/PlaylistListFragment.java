package rs.ac.bg.etf.running.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentPlaylistListBinding;

@AndroidEntryPoint
public class PlaylistListFragment extends Fragment {

    private FragmentPlaylistListBinding binding;
    private PlaylistViewModel playlistViewModel;
    private NavController navController;
    private MainActivity mainActivity;

    public PlaylistListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        playlistViewModel = new ViewModelProvider(mainActivity).get(PlaylistViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlaylistListBinding.inflate(inflater, container, false);

        PlaylistAdapter playlistAdapter = new PlaylistAdapter(mainActivity);
        playlistViewModel.getPlaylists().observe(
                getViewLifecycleOwner(),
                playlistAdapter::setPlaylistList);

        binding.recyclerViewPlaylistList.setAdapter(playlistAdapter);
        binding.recyclerViewPlaylistList.setLayoutManager(new LinearLayoutManager(mainActivity));

        binding.newPlaylistButton.setOnClickListener(view -> {
            navController.navigate(PlaylistListFragmentDirections.createPlaylist());
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }
}