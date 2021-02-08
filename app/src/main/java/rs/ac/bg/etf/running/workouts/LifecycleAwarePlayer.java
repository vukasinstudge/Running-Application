package rs.ac.bg.etf.running.workouts;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.music.PlaylistViewModel;

public class LifecycleAwarePlayer implements DefaultLifecycleObserver {

    private MediaPlayer mediaPlayer = null;
    private Context currContext;

    private static MediaPlayer staticMediaPlayer = null;

    public static MediaPlayer getStaticMediaPlayer() {
        return staticMediaPlayer;
    }

    public static void setStaticMediaPlayer(MediaPlayer staticMediaPlayer) {
        LifecycleAwarePlayer.staticMediaPlayer = staticMediaPlayer;
    }

    @Inject
    public LifecycleAwarePlayer() {

    }

    public void start(Context context) {
        currContext = context;
        int index = 0;
        String songs = MainActivity.getCurrPlaylist().getSongs();
        if (mediaPlayer == null) {
            try {
                String song = "";
                int num = songs.charAt(0) - '0';
                for (String strFile : context.getFilesDir().list()) {
                    if (num == index) song = strFile;
                    index++;
                }
                String path = context.getFilesDir().getAbsolutePath() + File.separator + song;
                mediaPlayer = new MediaPlayer();
                setStaticMediaPlayer(mediaPlayer);
                mediaPlayer.setDataSource(path);
                mediaPlayer.setOnPreparedListener(mp -> {
                    int duration = mediaPlayer.getDuration();
                    mediaPlayer.start();

                    Log.d("trajanje", "" + duration);

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


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
