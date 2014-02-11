package alex.hw01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	

	private static final int REQUEST_CODE = 1;
	private TextView tv;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView) findViewById(R.id.tvTextOut);
		
	}


	public void onClickSend(View v) {
		
		Intent intent = new Intent(this, TwoActivity.class);
		startActivityForResult(intent, REQUEST_CODE);
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
			
			tv.setText("" + data.getStringExtra("text"));
			
		}
		
	}
	

}
