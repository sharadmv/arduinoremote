package com.sharad.blimpcontrol;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.getbridge.bridge.Bridge;
import com.getbridge.bridge.BridgeRemoteObject;

public class Blimp extends Activity implements SensorEventListener {
	Button accelerate, reverse;
	Bridge bridge;
	Caradson caradson;

	private SensorManager sm;
	private PowerManager pm;
	private WindowManager wm;
	private Display display;
	private float sX, sY;
	private long sTimestamp;
	private Sensor accel;
	private WakeLock wl;
	private boolean leftOn, rightOn;

	private boolean accelerating = false, reversing = false;

	interface Caradson extends BridgeRemoteObject {
		public void accelerate();

		public void reverse();

		public void leftOn();

		public void leftOff();

		public void rightOn();

		public void rightOff();

		public void stop();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		bridge = new Bridge("0d21e491ce3a2af4");
		try {
			bridge.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		caradson = bridge.getService("caradson", Caradson.class);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blimp);
		accelerate = (Button) findViewById(R.id.accelerate);
		reverse = (Button) findViewById(R.id.reverse);
		accelerate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (!accelerating) {
					caradson.accelerate();
					accelerating = true;
				}
				return false;
			}
		});
		accelerate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				accelerating = false;
				caradson.stop();
			}
		});
		reverse.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (!reversing) {
					caradson.reverse();
					reversing = true;
				}
				return false;
			}
		});
		reverse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				reversing = false;
				caradson.stop();
			}
		});

		sm = (SensorManager) getSystemService(SENSOR_SERVICE);

		pm = (PowerManager) getSystemService(POWER_SERVICE);

		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		display = wm.getDefaultDisplay();

		wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass()
				.getName());

		accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_blimp, menu);
		return true;
	}

	public void toast(String text) {
		Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;
		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			sX = event.values[0];
			sY = event.values[1];

			break;
		case Surface.ROTATION_90:
			sX = -event.values[1];
			sY = event.values[0];

			break;
		case Surface.ROTATION_180:
			sX = -event.values[0];
			sY = -event.values[1];

			break;
		case Surface.ROTATION_270:
			sX = event.values[1];
			sY = -event.values[0];

			break;

		}
		if (sX > 2) {
			if (!leftOn) {
				caradson.leftOn();
				Log.d("accel", "left");
				leftOn = true;
			}
		} else {
			if (leftOn) {
				caradson.leftOff();
				leftOn = false;
			}
		}
		if (sX < -2) {
			if (!rightOn) {
				rightOn = true;
				Log.d("accel", "right");
				caradson.rightOn();
			}
		} else {
			if (rightOn) {
				caradson.rightOff();
				rightOn = false;
			}
		}

		accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		sTimestamp = event.timestamp;

	}
}
