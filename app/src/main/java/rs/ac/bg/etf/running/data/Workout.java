package rs.ac.bg.etf.running.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Workout {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private Date date;
    private String label;
    private double distance;
    private double duration;
    String username;
    private int numSteps;

    public Workout(long id, Date date, String label, double distance, double duration, String username, int numSteps) {
        this.id = id;
        this.date = date;
        this.label = label;
        this.distance = distance;
        this.duration = duration;
        this.username = username;
        this.numSteps = numSteps;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumSteps() {
        return numSteps;
    }

    public void setNumSteps(int numSteps) {
        this.numSteps = numSteps;
    }
}
