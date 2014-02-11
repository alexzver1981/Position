package alex.position;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Toast;

public class MyPreferenceScreen extends PreferenceActivity {

	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		addPreferencesFromResource(R.xml.mypref);
	}
	
	
	public void onClickClear (View v) {

        SQLiteDatabase db = new MyDB(getApplicationContext()).getWritableDatabase();
    	
    	Cursor cursor = db.query("dbLocation", null, null, null, null, null, null);
    	
    	if(cursor.getCount() != 0) db.delete("dbLocation", null, null);
    	
    	cursor.close();
    	db.close();
    	
    	Toast.makeText(getApplicationContext(), "Ok", Toast.LENGTH_SHORT).show();    	
	}
	
	
	public void onClickClearAbonent (View v) {

        SQLiteDatabase db = new MyDbAbonent(getApplicationContext()).getWritableDatabase();
    	
    	Cursor cursor = db.query("dbLocationAbonent", null, null, null, null, null, null);
    	
    	if(cursor.getCount() != 0) db.delete("dbLocationAbonent", null, null);
    	
    	cursor.close();
    	db.close();
    	
    	Toast.makeText(getApplicationContext(), "Ok", Toast.LENGTH_SHORT).show();		
	}
	
	
	
}
