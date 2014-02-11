package alex.position;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbAbonent extends SQLiteOpenHelper {

	public MyDbAbonent(Context context) {
		super(context, "mydbabonent1", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("create table dbLocationAbonent (_id integer primary key autoincrement," +
				" latitude text, longitude text, time text);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
