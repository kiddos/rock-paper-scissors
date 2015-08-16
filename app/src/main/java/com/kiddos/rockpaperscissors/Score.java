package com.kiddos.rockpaperscissors;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class Score extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);

		ListView record = (ListView) findViewById(R.id.lvScore);

		Intent i = getIntent();
		Bundle bundle = i.getExtras();
		try {
			int[] myScore = bundle.getIntArray("MyScore");
			int[] androidScore = bundle.getIntArray("AndroidScore");

			RecordAdapter recordAdapter = new RecordAdapter(Score.this, myScore, androidScore);
			record.setAdapter(recordAdapter);
		}catch(Exception e) {
			Log.d("Score Activity", e.toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_score, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_exit) {
			return true;
		} else if (id == R.id.action_keep_playing) {
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	private class RecordAdapter extends BaseAdapter {
		private Context context;
		private int[] myRecord, androidRecord;
		private LayoutInflater layoutInflater;

		public RecordAdapter(Context context, int[] myRecord, int[] androidRecord) {
			this.context = context;
			this.myRecord = myRecord;
			this.androidRecord = androidRecord;

			layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return myRecord.length;
		}

		@Override
		public Object getItem(int position) {
			return new int[]{myRecord[position], androidRecord[position]};
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = layoutInflater.inflate(R.layout.list_view_item, null);
			}

			TextView gameNumber = (TextView) v.findViewById(R.id.tvGameNumber);
			TextView myScore = (TextView) v.findViewById(R.id.tvMyScore);
			TextView androidScore = (TextView) v.findViewById(R.id.tvAndroidScore);

			gameNumber.setText(String.format(Locale.CANADA, "Game %03d", position+1));
			myScore.setText(String.valueOf(this.myRecord[position]));
			androidScore.setText(String.valueOf(this.androidRecord[position]));

			return v;
		}
	}
}
