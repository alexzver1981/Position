package alex.position;

import java.util.List;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MyMapActivity extends Activity {
	
	private final int DIALOG_NOT_FOUND_INTERNET = 1;
	
	private MapView map;
	private TextView tvAdres;
	private Button btBack;
	private Button btNext;
	private double lon;
	private double lat;
	private boolean adresIsChecked;
	private String time = "";
	private String adres = "";
	private String namedb;
	private int count;
	private int position;
	private SQLiteDatabase db;
	private Cursor cursor;
	private MapController controller;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		map = (MapView) findViewById(R.id.map);
		
		// задаём кнопки увеличения и уменьшения масштаба
		map.showBuiltInScreenButtons(true);
		
		tvAdres = (TextView) findViewById(R.id.tvAdres);
		
		btBack = (Button) findViewById(R.id.btBack);
		btNext = (Button) findViewById(R.id.btNext);
		
		controller = map.getMapController();
		controller.setZoomCurrent(17);
	}

	
	@Override
	protected void onStart() {
		super.onStart();	

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		adresIsChecked = pref.getBoolean("adres", false);
		
        Intent intent = getIntent();
		
		String longitude = intent.getStringExtra("lon");
		String latitude = intent.getStringExtra("lat");
		time = intent.getStringExtra("time");
	
		lon = Double.parseDouble(longitude);
		lat = Double.parseDouble(latitude);

		position = intent.getIntExtra("position", 1);
		namedb = intent.getStringExtra("namedb");
		
		if (namedb.equals("dbLocationAbonent")) db = new MyDbAbonent(getApplicationContext()).getWritableDatabase();
		
		else db = new MyDB(getApplicationContext()).getWritableDatabase();
		
		cursor = db.query(namedb, null, null, null, null, null, null);		
		count = cursor.getCount();
		
		onShowMap();
	}
	
	
	private void onShowMap(){
		   
		if (adresIsChecked) {
			
			getAdresAndShowMap();		
		}
		
		else showMap();
	}
	
	
	private void getAdresAndShowMap () {
				
		 final ProgressDialog progressDialog = new ProgressDialog(this);
		 
			new AsyncTask<Void, Void, String>() {
				
				@Override
				protected void onPreExecute() {
				
				//	adres = "";
					
				    progressDialog.setMessage(getResources().getString(R.string.wait));		 
				    progressDialog.show();
				}

				
				@Override
				protected String doInBackground(Void... params) {
					
					Geocoder geocoder = new Geocoder(getApplicationContext());
					
					String newAdres = "";
					
					try {
						
						List<Address> list = geocoder.getFromLocation(lat, lon, 1);
						
						if (list.size() != 0) {
							
							Address adr = list.get(0);
							newAdres = adr.getAddressLine(0);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					
					return newAdres;
				}
				
				
				@SuppressWarnings("deprecation")
				@Override
				   protected void onPostExecute(String result) {
				   
				    progressDialog.hide();
				    
				    if (result.equals("")) {
				    	
				    	showDialog(DIALOG_NOT_FOUND_INTERNET);
				    }
				    
				    adres = result;
				    showMap();
				   }
				
			}.execute();
		
	}
	
	
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {

		switch (id) {
		
		case DIALOG_NOT_FOUND_INTERNET:
			

	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	
	    	builder.setMessage(R.string.alert_dialog_message);
	    	builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
	    	
	    	return builder.create();

		}

		return null;
	}
	
	
	private void showMap() {
		
		String adresMap = time + "\n" + adres;
		tvAdres.setText(adresMap);
					
		controller.setPositionAnimationTo(new GeoPoint(lat, lon));
		
		OverlayManager manager = controller.getOverlayManager();		
		manager.getMyLocation().setEnabled(false);
		
		Resources resource = getResources();
		
		OverlayItem overlayItem = new OverlayItem(new GeoPoint(lat, lon), resource.getDrawable(R.drawable.a));
		
		BalloonItem balloonItem = new BalloonItem(getApplicationContext(), overlayItem.getGeoPoint());
		balloonItem.setText(adresMap);
		
		overlayItem.setBalloonItem(balloonItem);
		
		Overlay overlay = new Overlay(controller);
		overlay.addOverlayItem(overlayItem);
		
		manager.addOverlay(overlay);		
	}
	
	
	public void onClickBack(View v) {
		
		if (position > 0) {
			
			position --;
			
			btNext.setEnabled(true);
			
			getParametrs();
			onShowMap();
		}
		
		else btBack.setEnabled(false);	
	}
	
	
	public void onClickNext(View v) {
		
        if (position < (count-1)) {
			
        	position ++;
        	
			btBack.setEnabled(true);
			
			getParametrs();
			onShowMap();
		}
		
		else btNext.setEnabled(false);		
	}
	
	
	private void getParametrs() {
		
		cursor = db.query(namedb, null, null, null, null, null, null);		
    	cursor.moveToPosition(position);
 
    	lon = Double.parseDouble(cursor.getString(cursor.getColumnIndex("longitude")));
		lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex("latitude")));
		time = cursor.getString(cursor.getColumnIndex("time"));
	}
	
	
		
	@Override
	protected void onStop() {
		super.onStop();
		
		cursor.close();
    	db.close();
	}

}
