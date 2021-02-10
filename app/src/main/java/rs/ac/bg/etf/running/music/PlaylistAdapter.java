package rs.ac.bg.etf.running.music;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.databinding.ViewHolderPlaylistBinding;

public class PlaylistAdapter extends RecyclerView.Adapter<rs.ac.bg.etf.running.music.PlaylistAdapter.PlaylistViewHolder> {

    private List<Playlist> playlistList = new ArrayList<>();
    private PlaylistViewModel playlistViewModel;
    private MainActivity mainActivity;

    public PlaylistAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        playlistViewModel = new ViewModelProvider(mainActivity).get(PlaylistViewModel.class);
    }

    public void setPlaylistList(List<Playlist> playlistList) {
        this.playlistList = playlistList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistAdapter.PlaylistViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolderPlaylistBinding viewHolderPlaylistBinding = ViewHolderPlaylistBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new PlaylistAdapter.PlaylistViewHolder(viewHolderPlaylistBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull rs.ac.bg.etf.running.music.PlaylistAdapter.PlaylistViewHolder holder, int position) {
        holder.bind(playlistList.get(position));
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {

        public ViewHolderPlaylistBinding binding;

        public PlaylistViewHolder(@NonNull ViewHolderPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Playlist playlist) {
            binding.playlistName.setText(playlist.getName());
            String[] songsSplit = playlist.getSongs().split("/");
            binding.playlistSongNumber.setText(songsSplit.length + " songs");
            binding.chooseButton.setOnClickListener(view -> {
                playlistViewModel.setCurrentPlaylist(playlist);
                MainActivity.setCurrPlaylist(playlist);
                Toast.makeText(MainActivity.getStaticMain(), "You chose " + playlist.getName(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
