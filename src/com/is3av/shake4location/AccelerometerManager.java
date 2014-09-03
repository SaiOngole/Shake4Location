package com.is3av.shake4location;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class AccelerometerManager {

	private static Context aContext=null;


	/** Accuracy configuration */
	private static float threshold  = 15.0f; 
	private static int interval     = 200;

	private static Sensor sensor;
	private static SensorManager sensorManager;
	private static AccelerometerListener listener;
	
	

	/** indicates whether or not Accelerometer Sensor is supported */
	private static Boolean supported;
	/** indicates whether or not Accelerometer Sensor is running */
	private static boolean running = false;

	/**
	 * Returns true if the manager is listening to orientation changes
	 */
	public static boolean isListening() {
		return running;
	}

	/**
	 * Unregisters listeners
	 */
	public static void stopListening() {
		running = false;
		try {
			if (sensorManager != null && sensorEventListener != null) {
				sensorManager.unregisterListener(sensorEventListener);
			}
		} catch (Exception e) {}
	}

	/**
	 * Returns true if at least one Accelerometer sensor is available
	 */
	@SuppressLint("UseValueOf")
	public static boolean isSupported(Context context) {
		aContext = context;
		if (supported == null) {
			if (aContext != null) {


				sensorManager = (SensorManager) aContext.
						getSystemService(Context.SENSOR_SERVICE);

				
				List<Sensor> sensors = sensorManager.getSensorList(
						Sensor.TYPE_ACCELEROMETER);

				supported = new Boolean(sensors.size() > 0);

			} else {
				supported = Boolean.FALSE;
			}
		}
		return supported;
	}

	/**
	 * Configure the listener for shaking
	 * @param threshold
	 *             minimum acceleration variation for considering shaking
	 * @param interval
	 *             minimum interval between to shake events
	 */
	public static void configure(int threshold, int interval) {
		AccelerometerManager.threshold = threshold;
		AccelerometerManager.interval = interval;
	}

	/**
	 * Registers a listener and start listening
	 * @param accelerometerListener
	 *             callback for accelerometer events
	 */
	public static void startListening( AccelerometerListener accelerometerListener ) 
	{

		sensorManager = (SensorManager) aContext.
				getSystemService(Context.SENSOR_SERVICE);
		
		List<Sensor> sensors = sensorManager.getSensorList(
				Sensor.TYPE_ACCELEROMETER);

		if (sensors.size() > 0) {

			sensor = sensors.get(0);

			// Register Accelerometer Listener
			running = sensorManager.registerListener(
					sensorEventListener, sensor, 
					SensorManager.SENSOR_DELAY_GAME);

			listener = accelerometerListener;
		}


	}

	/**
	 * Configures threshold and interval
	 * And registers a listener and start listening
	 * @param accelerometerListener
	 *             callback for accelerometer events
	 * @param threshold
	 *             minimum acceleration variation for considering shaking
	 * @param interval
	 *             minimum interval between to shake events
	 */
	public static void startListening(
			AccelerometerListener accelerometerListener, 
			int threshold, int interval) {
		configure(threshold, interval);
		startListening(accelerometerListener);
	}

	/**
	 * The listener that listen to events from the accelerometer listener
	 */
	private static SensorEventListener sensorEventListener = 
			new SensorEventListener() {

		private long now = 0;
		private long timeDiff = 0;
		private long lastUpdate = 0;
		private long lastShake = 0;

		private float x = 0;
		private float y = 0;
		private float z = 0;
		private float lastX = 0;
		private float lastY = 0;
		private float lastZ = 0;
		private float force = 0;

		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		public void onSensorChanged(SensorEvent event) {
		//using the timestamp as reference
			now = event.timestamp;

			x = event.values[0];
			
			y = event.values[1];
			
			z = event.values[2];
			
			
			if (lastUpdate == 0) {
				lastUpdate = now;
				lastShake = now;
				lastX = x;
				lastY = y;
				lastZ = z;
				

			} else {
				timeDiff = now - lastUpdate;

				if (timeDiff > 0) { 

					force = Math.abs(x + y + z - lastX - lastY - lastZ);

					if (Float.compare(force, threshold) >0 ) {
					

						if (now - lastShake >= interval) { 

							// trigger shake event
							listener.onShake(force,x,y,z);
						}
						else
						{
					
						}
						lastShake = now;
					}
					lastX = x;
					lastY = y;
					lastZ = z;
					lastUpdate = now; 
				}
				else
				{
					

				}
			}
			// trigger change event
			listener.onAccelerationChanged(x, y, z);
		}

	};
	
}