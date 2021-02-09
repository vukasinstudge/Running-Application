package rs.ac.bg.etf.running.workouts;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;

public class LifecycleAwareStepCounter implements DefaultLifecycleObserver {

    private SensorManager sensorManager = null;

    private final SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            TextView textView = (TextView) MainActivity.getStaticMain().findViewById(R.id.steps);
            String stepsString = textView.getText().toString();
            int stepsNum = Integer.parseInt(stepsString.split(":")[1].substring(1));
            stepsNum++;
            textView.setText("Steps: " + stepsNum);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Inject
    public LifecycleAwareStepCounter() {

    }

    public void start(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (sensor != null) {
            sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(context, "No sensor for step counting!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if (sensorManager != null) {
            sensorManager.unregisterListener(listener);
        }
    }
}
