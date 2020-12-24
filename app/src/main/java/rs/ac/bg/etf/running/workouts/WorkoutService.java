package rs.ac.bg.etf.running.workouts;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;

public class WorkoutService extends Service {

    private final Timer timer = new Timer();

    private boolean serviceStarted = false;

    private void scheduleTimer() {
        Handler handler = new Handler(Looper.getMainLooper());

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> Toast.makeText(
                        WorkoutService.this,
                        getResources().getStringArray(R.array.workout_toast_motivation)[0],
                        Toast.LENGTH_SHORT).show());
            }
        }, 0, 7000);

        serviceStarted = true;
    }

    @Override
    public void onCreate() {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onStartCommand()");
        if (!serviceStarted) {
            scheduleTimer();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onBind()");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onDestroy()");
        super.onDestroy();
        timer.cancel();
    }
}
