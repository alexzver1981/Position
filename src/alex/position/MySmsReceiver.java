package alex.position;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;

@SuppressWarnings("deprecation")
public class MySmsReceiver extends BroadcastReceiver {

	private String text;
	private String telNumber;
	private String outTextMessage;
	private SmsManager manager;
	private Context context;
	private Intent intent;
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		this.context = context;
		this.intent = intent;
		
		String action = intent.getAction();
		
		if (action == "android.provider.Telephony.SMS_RECEIVED") {
			
			smsService();			
		}
		
		if (action == "android.intent.action.BOOT_COMPLETED") {
						
			startLocatinService();
		}
			
	}

	
	private void startLocatinService() {
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean ischecked = pref.getBoolean("start", false);
		
		if (ischecked) {
			
			int time = Integer.parseInt(pref.getString("time_update", "1"));
			int distance = Integer.parseInt(pref.getString("distanse_update", "50"));
			
			context.stopService(new Intent(context, MyService.class));
			
			Intent intentService = new Intent(context, MyService.class);
			intent.putExtra("time", time * 1000);
			intent.putExtra("distance", distance);
			
			context.startService(intentService);
		}
	}
	
	
	private void smsService() {
		
		Bundle bundle = intent.getExtras();
		
	    if (bundle != null) {	
	    	
	    	Object[] pdus = (Object[]) bundle.get("pdus");
	    	
	    	for (Object pdu: pdus) {
	    		
	    		SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);	    	
	    		text = sms.getMessageBody();	    		
	    		telNumber = sms.getOriginatingAddress();
	    	}    	
	    }
	    

       if (text.contains("getmecoordinats")) {
    	   
    	   returnCoordinats();
		}
		
		if (text.contains("lat: ")){
			
			saveCoordinats();		
		}
		
	}
	
	
	private void returnCoordinats() {
		
		SQLiteDatabase db = new MyDB(context).getWritableDatabase();
	
        String[] columns = {"latitude", "longitude", "time"};
		
		Cursor cursor = db.query("dbLocation", columns, null, null, null, null, null);
		
		int position = cursor.getCount();
    		
		if (position != 0) {
			
			cursor.moveToLast();
  
	   		String lon = cursor.getString(cursor.getColumnIndex("longitude"));
	   		String lat = cursor.getString(cursor.getColumnIndex("latitude"));
	   		String time = cursor.getString(cursor.getColumnIndex("time"));
	   		
	   		cursor.close();
	   		db.close();
	    	
			outTextMessage = "lat: " + lat + " lon: " + lon + " time " + time + " ";
		}
				
		else outTextMessage = "xCode: ";
		
		manager = SmsManager.getDefault();

		if (!telNumber.equals("")){
    		
    		try {
    			
    			if (PhoneNumberUtils.isWellFormedSmsAddress(telNumber)) {
    				
    				manager.sendTextMessage(telNumber, null, outTextMessage, null, null);
    			}  			
        	}
        	
        	catch (Exception e) {
        		
        	}   		
    	}		
	}
	
	
	private void saveCoordinats() {
		
    	String subString = "";
    	
    	ArrayList<String> params = new ArrayList<String>();
    	
    	char element;
    	int index = 5;
    	
    	for (int i = index; i < text.length(); i++) {
    		
    		element = text.charAt(i);
    		
    		if (element == ' '){
    			
    			subString = text.substring(index, i); 
    			params.add(subString);   			
    			index = i + 6;
    			i=index;  			
    		}   		
    	}
    	
    	if (params.size() == 3) {
    		
    		SQLiteDatabase db = new MyDbAbonent(context).getWritableDatabase();

    		ContentValues cv = new ContentValues();

    		cv.put("latitude", params.get(0));
    		cv.put("longitude", params.get(1));
    		cv.put("time", params.get(2));
    	
    		db.insert("dbLocationAbonent", null, cv);
    		db.close();  		
    	}	
	}
	
	
	
}
