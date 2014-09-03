package com.is3av.shake4location;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.is3av.accelerometer.R;


public class AddressActivity extends Activity implements AccelerometerListener{
	private TextView instruction;
	private TextView addressText;
	Location currentLocation;
	double currentLatitude;
	double currentLongitude;
	private static ArrayList<Float> xCo = new ArrayList<Float>();
	private static ArrayList<Float> yCo = new ArrayList<Float>();
	private static ArrayList<Float> zCo = new ArrayList<Float>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		instruction = (TextView) findViewById(R.id.text);
		instruction.setText("Shake the Device to get current address");
		addressText = (TextView) findViewById(R.id.address);
		addressText.setText(" ");
		
		checkGPS();	
		
		LocationManager locationManager = 
				(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				updateLocation(location);
			}
			public void onStatusChanged(
					String provider, int status, Bundle extras) {}
			public void onProviderEnabled(String provider) {}
			public void onProviderDisabled(String provider) {}
		};

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	public void onAccelerationChanged(float x, float y, float z) {
		// TODO Auto-generated method stub

	}

	public void onShake(float force,float x,float y,float z) {
		try {
			xCo.add(x);
			yCo.add(y);
			zCo.add(z);
			saveValues(xCo,yCo,zCo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			getAddress();  	 		
		

	}

	void getAddress(){
		try{
			Geocoder gcd = new Geocoder(this, Locale.getDefault());

			List<Address> addresses = gcd.getFromLocation(currentLatitude, currentLongitude, 1);
			if(addresses != null) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
				for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
				}
				String ret = strReturnedAddress.toString();
				addressText.setText(ret);

			}


		}
		catch(IOException ex){
			addressText.setText(ex.getMessage().toString());
		}
	}

	void updateLocation(Location location){
		currentLocation = location;
		currentLatitude = currentLocation.getLatitude();
		currentLongitude = currentLocation.getLongitude();

	}


	@Override
	public void onResume() {
		super.onResume();

		// If accelerometer is available, start listening
		if (AccelerometerManager.isSupported(this)) {

		
			AccelerometerManager.startListening(this);
		}
	}

	@Override
	public void onStop() {
		super.onStop();

		// If accelerometer is listening, stop it.
		if (AccelerometerManager.isListening()) {
			
			AccelerometerManager.stopListening();

		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("Sensor", "Service  distroy");
		
		// If accelerometer is listening, stop it.
		if (AccelerometerManager.isListening()) {

			AccelerometerManager.stopListening();

		}

	}
	// Creating a file and saving the values of the three co-ordinates in it whenever the device is shaked
	private static void saveValues(ArrayList<Float> xCo,ArrayList<Float> yCo,ArrayList<Float> zCo) throws IOException {
		String root = android.os.Environment.getExternalStorageDirectory().toString();
		File mydir = new File(root+"/values");
		mydir.mkdirs();
		String fileName = "accel_values" + ".txt";
		File file = new File(mydir+File.separator+fileName);
		FileWriter writer = new FileWriter(file);
		writer.append("X-Coordinates"+"\n");
		writer.append(xCo.toString());
		writer.flush();
		writer.append("\n"+"Y-Coordinates"+"\n");
		writer.append(yCo.toString());
		writer.flush();
		writer.append("\n"+"Z-Coordinates"+"\n");
		writer.append(zCo.toString());
		writer.flush();
		writer.close();
		
		
	}
	@SuppressWarnings("deprecation")
	private void checkGPS() {
		final String provider = Settings.Secure.getString(getContentResolver(),Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
 
			// set title
			alertDialogBuilder.setTitle("Enable GPS");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Please turn on GPS to use the application")
				.setCancelable(false)
				.setPositiveButton("Turn On",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						
						if(provider.equals("")) {
							Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(intent);
						}
							
					}
				  })
				.setNegativeButton("Close Application",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						
						finish();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
			}
		
	}
