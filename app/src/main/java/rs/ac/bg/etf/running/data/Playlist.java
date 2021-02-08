package rs.ac.bg.etf.running.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Playlist {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String songs;
    private String username;

    public Playlist(long id, String name, String songs, String username) {
        this.id = id;
        this.name = name;
        this.songs = songs;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSongs() {
        return songs;
    }

    public void setSongs(String songs) {
        this.songs = songs;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
