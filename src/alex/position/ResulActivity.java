package alex.position;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class ResulActivity extends Activity implements OnItemClickListener {
	
	private final int MY_COORDINATS = 1;
	private String namedb;
	private int coordinats;
	private TextView tv;
	private ListView lv;
	private SQLiteDatabase db;
	private Cursor cursor;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		tv = (TextView) findViewById(R.id.text);
		lv = (ListView) findViewById(R.id.lv);		
		
	}

	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		MyAdapter adapter = new MyAdapter();
		
		Intent intent = getIntent();		
		coordinats = intent.getIntExtra("coordinats", 2);
		
		if (coordinats == MY_COORDINATS) {
			
			namedb = "dbLocation";		
			db = new MyDB(getApplicationContext()).getWritableDatabase();						
		}
		
		else {
			
			namedb = "dbLocationAbonent";			
			db = new MyDbAbonent(getApplicationContext()).getWritableDatabase();						
		}

		cursor = db.query(namedb, null, null, null, null, null, null);
		
        if (cursor.moveToFirst()) {
        	
        	do {
				
        		adapter.addItem(cursor.getString(cursor.getColumnIndex("time")));
        		
			} while (cursor.moveToNext());
			
        	lv.setAdapter(adapter);
			lv.setOnItemClickListener(this);
			
			tv.setText("Результаты");
		}
		
		else tv.setText("Результатов нет");		
		
	}
	
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		
		cursor = db.query(namedb, null, null, null, null, null, null);

    	if(cursor.getCount() != 0) {
    		
    		cursor.moveToPosition(position);
    		
    		String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
    		String longitude = cursor.getString(cursor.getColumnIndex("longitude"));
    		String time = cursor.getString(cursor.getColumnIndex("time"));
    		
    		
    		Intent intent = new Intent(getApplicationContext(), MyMapActivity.class);
    		intent.putExtra("lat", latitude);
    		intent.putExtra("lon", longitude);
    		intent.putExtra("time", time);
    		intent.putExtra("position", position);
    		intent.putExtra("namedb", namedb);
    		
    		startActivity(intent); 
    	}	
	}
		

	@Override
	protected void onPause() {
		super.onPause();

		cursor.close();
    	db.close();
	}
	
	
	
	class MyAdapter extends BaseAdapter {

        ArrayList<String> listItems = new ArrayList<String>();
		
        public void addItem(String item){
	
			listItems.add(item);
			notifyDataSetChanged();
		}
        
        
		@Override
		public int getCount() {
			
			return listItems.size();
		}

		@Override
		public Object getItem(int position) {
			
			return listItems.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			
			if (convertView == null){
				
			    convertView = getLayoutInflater().inflate(R.layout.adapter_result, null);
			    
			    holder = new ViewHolder();
			    holder.text = (TextView) convertView.findViewById(R.id.tvRes);
			    
			    convertView.setTag(holder);
			}
			
			else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.text.setText(listItems.get(position));
			
			return convertView;
		}

	}
	
	
	class ViewHolder {
	
		TextView text;
	}
	
	
		
}
