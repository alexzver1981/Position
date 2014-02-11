package alex.position;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.gsm.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

	private static final int time_duration = 4000;
	private static final int ID_CANCEL = 2;
	private static final int ID_PREFERENCE = 1;
	private final int MY_COORDINATS = 1;
	private final int ANOTHER_COORDINATS = 2;
	private SharedPreferences pref;
	private SmsManager manager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		manager = SmsManager.getDefault();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	
		menu.add(0, ID_PREFERENCE, 0, "Настройки");
		menu.add(0, ID_CANCEL, 0, "Отмена");
		
		return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		switch (item.getItemId()) {
		case ID_PREFERENCE:
			
			startActivity(new Intent(getApplicationContext(), MyPreferenceScreen.class));		
			break;

			
		case ID_CANCEL:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	public void onClickButtonStart(View v) {
		
		int time = Integer.parseInt(pref.getString("time_update", "1"));
		int distance = Integer.parseInt(pref.getString("distanse_update", "50"));
		
		stopService(new Intent(this, MyService.class));
		
		Intent intent = new Intent(this, MyService.class);
		intent.putExtra("time", time * 1000);
		intent.putExtra("distance", distance);
		
		startService(intent);
	}

	
    public void onClickButtonStop(View v) {
   	
    	stopService(new Intent(this, MyService.class));
	}
	
    
    public void onClickButtonMyCoordinats(View v) {
	
    	Intent intent = new Intent(this, ResulActivity.class);
    	intent.putExtra("coordinats", MY_COORDINATS);
    	startActivity(intent);   	
	}
    
    
    public void onClickButtonAnotherCoordinats(View v) {
   	
    	Intent intent = new Intent(this, ResulActivity.class);
    	intent.putExtra("coordinats", ANOTHER_COORDINATS);
    	startActivity(intent);   	
	}
    
    
	public void onClickButtonGetMeCoordinats(View v) {
    	
    	String telNumber = pref.getString("tel", "");
    	String smsText = "getmecoordinats";
    	
    	if (!telNumber.equals("")){
    		
    		try {
    			
    			if (PhoneNumberUtils.isWellFormedSmsAddress(telNumber)) {
    				
    				manager.sendTextMessage(telNumber, null, smsText, null, null);
    				
    				Toast.makeText(getApplicationContext(), "Запрос координат отправлен на номер \n" + 
    				telNumber + " \n дождитесь прихода SMS", time_duration).show();   				
    			}
        		
    			else Toast.makeText(getApplicationContext(), "Ошибка отправки SMS", Toast.LENGTH_SHORT).show();   			
        	}
        	
        	catch (Exception e) {
        		
        		Toast.makeText(getApplicationContext(), "Ошибка отправки SMS", Toast.LENGTH_SHORT).show();
        	}    		
    	}
    	
    	else Toast.makeText(getApplicationContext(), "Задайде номер телефона в настройках", Toast.LENGTH_SHORT).show();	
    }
    
	
    public void onClickButtonSettings(View v) {
    	
    	startActivity(new Intent(getApplicationContext(), MyPreferenceScreen.class));
    	
    
    }
    
    
    
    
}
