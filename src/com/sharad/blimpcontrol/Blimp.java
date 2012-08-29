package com.sharad.blimpcontrol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.Toast;

public class Blimp extends Activity implements SensorEventListener {
	Button accelerate, reverse, auto;
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

	private boolean accelerating = false, reversing = false, autoing = false;

	class Caradson {
		public void accelerate() {
			new RequestTask().execute(getIp() + "/api/accelerate/true");
		}

		public void auto() {
			new RequestTask().execute(getIp() + "/api/auto/true");
		}

		public void reverse() {
			new RequestTask().execute(getIp() + "/api/reverse/true");
		}

		public void leftOn() {
			new RequestTask().execute(getIp() + "/api/left/true");
		}

		public void leftOff() {
			new RequestTask().execute(getIp() + "/api/left/false");
		}

		public void rightOn() {
			new RequestTask().execute(getIp() + "/api/right/true");
		}

		public void rightOff() {
			new RequestTask().execute(getIp() + "/api/right/false");
		}

		public void stop() {
			new RequestTask().execute(getIp() + "/api/stop/true");
		}
	}

	public String getIp() {
		return "http://"
				+ ((EditText) findViewById(R.id.ip)).getText().toString();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		caradson = new Caradson();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blimp);
		accelerate = (Button) findViewById(R.id.accelerate);
		reverse = (Button) findViewById(R.id.reverse);
		auto = (Button) findViewById(R.id.auto);
		accelerate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (!accelerating) {
					Log.d("ACCELERATING", "awesome");
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
		auto.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (!autoing) {
					caradson.auto();
					autoing = true;
				}
				return false;
			}
		});
		auto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				autoing = false;
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

	class RequestTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... uri) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String responseString = null;
			try {
				response = httpclient.execute(new HttpGet(uri[0]));
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				// TODO Handle problems..
			} catch (IOException e) {
				// TODO Handle problems..
			}
			return responseString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Do anything with response..
		}
	}
}
