package alex.position;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class MyService extends Service implements LocationListener {

	
	private LocationManager manager;

	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		int time = intent.getIntExtra("time", 2000);
		int distance = intent.getIntExtra("distance", 300);
		
		Log.d("myLogs", "time = " + time);
		Log.d("myLogs", "distanse = " + distance);
		
		Toast.makeText(getApplicationContext(), "Пуск", Toast.LENGTH_SHORT).show();
		
		Log.d("myLogs", "onStartCommand");

		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long)time, distance, this);
	
		return START_REDELIVER_INTENT;
	}

	
	public void onDestroy() {
		super.onDestroy();
		Log.d("myLogs", "onDestroy");
		
		manager.removeUpdates(this);
		
		Toast.makeText(getApplicationContext(), "Остановлено", Toast.LENGTH_SHORT).show();
	}

	
	private void saveLocation(Location loc) {
	
		Toast.makeText(getApplicationContext(), "new Point", Toast.LENGTH_SHORT).show();
		
		if (loc != null) {

			String longitude = "" + loc.getLongitude();
			String latitude = "" + loc.getLatitude();
			
			SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss__dd,MM,yyyy");
			String time = sdfTime.format(new Date(System.currentTimeMillis()));
	
			SQLiteDatabase db = new MyDB(getApplicationContext()).getWritableDatabase();

			ContentValues cv = new ContentValues();

			cv.put("latitude", latitude);
			cv.put("longitude", longitude);
			cv.put("time", time);
			
			db.insert("dbLocation", null, cv);

			db.close();
		}
			
	}

	
	@Override
	public void onLocationChanged(Location location) {

		saveLocation(location);
	}

	
	@Override
	public void onProviderDisabled(String provider) {

	}

	
	@Override
	public void onProviderEnabled(String provider) {

	}

	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	
	}


			
	
}
