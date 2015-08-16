package com.kiddos.rockpaperscissors;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.io.File;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.*;


public class MainActivity extends Activity {
	private static final int NONE = -1;
	private static final int ROCK = 0;
	private static final int PAPER = 1;
	private static final int SCISSORS = 2;
	private static final String[] WINNING = {"Oh Yeah", "You Win", "Awesomeness", "God Like"};
	private static final String[] EVEN = {"...", "Draw", "OK", "Hum"};
	private static final String[] LOSE = {"Nah", "BOOOOO", "You Lose", "You Sucks"};
	private static final String RESULT = "wins\nthe series";
	private static final int NUMBER_OF_GAMES = 5;
	private ImageButton rock, paper, scissors;
	private TextView result, myScore, androidScore;
	private ImageView android, me;
	private ArrayList<Integer> myScoreRecord, androidScoreRecord;
	private boolean isLearning = false;
	private int[] series = {NONE, NONE, NONE, NONE, NONE};
	// machine learning
	MachineLearning ml;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		rock = (ImageButton) findViewById(R.id.ibRock);
		paper = (ImageButton) findViewById(R.id.ibPaper);
		scissors = (ImageButton) findViewById(R.id.ibScissors);
		result = (TextView) findViewById(R.id.tvResult);
		android = (ImageView) findViewById(R.id.ivAndroid);
		me = (ImageView) findViewById(R.id.ivMe);
		myScore = (TextView) findViewById(R.id.tvMe);
		androidScore = (TextView) findViewById(R.id.tvAndroid);
		CheckBox learning = (CheckBox) findViewById(R.id.cbLearning);

		// initialize
		myScoreRecord = new ArrayList<>();
		androidScoreRecord = new ArrayList<>();

		if (myScore.getText().toString().equals("")) {
			myScore.setText("0");
		}
		if (androidScore.getText().toString().equals("")) {
			androidScore.setText("0");
		}
		ml = new MachineLearning();

		// events
		ButtonHandler handler = new ButtonHandler();
		rock.setOnTouchListener(handler);
		paper.setOnTouchListener(handler);
		scissors.setOnTouchListener(handler);
		learning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				if (isChecked) {
					isLearning = true;
				} else {
					isLearning = false;
					ml.train();
				}
			}
		});
	}

	// button Handler
	private class ButtonHandler implements View.OnTouchListener {
		Bitmap clickedRock, clickedPaper, clickedScissors;
		Bitmap nonClickedRock, nonClickedPaper, nonClickedScissors;
		public ButtonHandler() {
			nonClickedRock = BitmapFactory.decodeResource(getResources(), R.drawable.rock);
			nonClickedPaper = BitmapFactory.decodeResource(getResources(), R.drawable.paper);
			nonClickedScissors = BitmapFactory.decodeResource(getResources(), R.drawable.scissors);
			clickedRock = createClickedBitmap(nonClickedRock);
			clickedPaper = createClickedBitmap(nonClickedPaper);
			clickedScissors = createClickedBitmap(nonClickedScissors);
		}

		private Bitmap createClickedBitmap(Bitmap src) {
			Bitmap clickedBitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(clickedBitmap);
			Paint paint = new Paint();
			paint.setColorFilter(new PorterDuffColorFilter(Color.argb(100, 0, 0, 0), PorterDuff.Mode.SRC_ATOP));
			canvas.drawBitmap(src, 0, 0, paint);
			return clickedBitmap;
		}

		private boolean isValidChoice(int choice) {
			int[] validChoice = {ROCK, PAPER, SCISSORS};
			for (int vc : validChoice) {
				if (vc == choice) return true;
			}
			return false;
		}

		// compute android's random choice
		private int getAndroidChoice() {
			if (!isLearning) {
				int myChoice = ml.predict(series);
				Log.d("Series", Arrays.toString(series));
				Log.d("ML predict my choice: ", "" + myChoice);
				if (isValidChoice(myChoice)) {
					int androidChoice = ROCK;
					if (myChoice == ROCK) {
						androidChoice = PAPER;
					} else if (myChoice == PAPER) {
						androidChoice = SCISSORS;
					} else if (myChoice == SCISSORS) {
						androidChoice = ROCK;
					}
					return androidChoice;
				}
			}
			int choice[] = {ROCK, PAPER, SCISSORS};
			Random r = new Random();
			return choice[r.nextInt(choice.length)];
		}

		private void addChoiceToSeries(int choice) {
			for (int i = 0 ; i < series.length ; i ++) {
				if (series[i] == NONE) {
					series[i] = choice;
					return;
				}
			}

			// else clear the series and add
			for (int i = 0 ; i < series.length ; i ++) {
				series[i] = NONE;
			}
			series[0] = choice;
		}

		// compute result
		private int compute(int android, int me) {
			int result = 0;
			if (me == android)
				result = 0;
			else if (me == ROCK && android == SCISSORS) {
				result = 1;
			} else if (me == ROCK && android == PAPER) {
				result = -1;
			} else if (me == PAPER && android == SCISSORS) {
				result = -1;
			} else if (me == PAPER && android == ROCK) {
				result = 1;
			} else if (me == SCISSORS && android == PAPER) {
				result = 1;
			} else if (me == SCISSORS && android == ROCK) {
				result = -1;
			}
			return result;
		}

		// get the random result text to display
		private String getResultText(int result) {
			Random r = new Random();
			if (result > 0) {
				return WINNING[r.nextInt(WINNING.length)];
			} else if (result == 0) {
				return EVEN[r.nextInt(EVEN.length)];
			} else {
				return LOSE[r.nextInt(LOSE.length)];
			}
		}

		// add the record
		private void addRecord(int myScore, int androidScore) {
			myScoreRecord.add(myScore);
			androidScoreRecord.add(androidScore);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// get android choice
			// *** NOTE ***
			// android choose BEFORE it even "knows" user's choice
			// don't even think that he cheated
			int androidChoice = getAndroidChoice();
			// get my choice
			int myChoice = 0;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				switch(v.getId()) {
					case R.id.ibRock:
						rock.setImageBitmap(clickedRock);
						myChoice = ROCK;
						break;
					case R.id.ibPaper:
						paper.setImageBitmap(clickedPaper);
						myChoice = PAPER;
						break;
					case R.id.ibScissors:
						scissors.setImageBitmap(clickedScissors);
						myChoice = SCISSORS;
						break;
				}

				// machine learning
				addChoiceToSeries(myChoice);
				if (isLearning) {
					ml.addVector(series, myChoice);
					Log.d("Series", Arrays.toString(series));
				}

				// display android's choice
				if (androidChoice == ROCK) {
					android.setImageResource(R.drawable.rock);
				} else if (androidChoice == PAPER) {
					android.setImageResource(R.drawable.paper);
				} else if (androidChoice == SCISSORS) {
					android.setImageResource(R.drawable.scissors);
				}
				// display my choice
				if (myChoice == ROCK) {
					me.setImageResource(R.drawable.rock);
				} else if (myChoice == PAPER) {
					me.setImageResource(R.drawable.paper);
				} else {
					me.setImageResource(R.drawable.scissors);
				}


				// compute result
				int result = compute(androidChoice, myChoice);
				MainActivity.this.result.setText(getResultText(result));

				// calculate rather the game should stop
				int m = Integer.parseInt(myScore.getText().toString());
				int a = Integer.parseInt(androidScore.getText().toString());
				if (result > 0) {
					m++;
					myScore.setText(String.valueOf(m));
				} else if (result < 0) {
					a++;
					androidScore.setText(String.valueOf(a));
				}

				int limit = NUMBER_OF_GAMES / 2 + 1;
				if (m >= limit) {
					MainActivity.this.result.setText("You\n" + RESULT);
					myScore.setText("0");
					androidScore.setText("0");

					addRecord(m, a);
				} else if (a >= limit) {
					MainActivity.this.result.setText("Android\n" + RESULT);
					myScore.setText("0");
					androidScore.setText("0");

					addRecord(m, a);
				}
			} else if(event.getAction() == MotionEvent.ACTION_UP){
				rock.setImageBitmap(nonClickedRock);
				paper.setImageBitmap(nonClickedPaper);
				scissors.setImageBitmap(nonClickedScissors);
			}
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	private int[] extractArrayList(ArrayList<Integer> a) {
		int[] list = new int[a.size()];
		for (int i = 0 ; i < a.size() ; i ++) {
			list[i] = a.get(i);
		}
		return list;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_exit) {
			finish();
			return true;
		} else if (id == R.id.action_score) {
			Intent intent = new Intent(MainActivity.this, Score.class);
			Bundle bundle = new Bundle();
			bundle.putIntArray("MyScore", extractArrayList(myScoreRecord));
			bundle.putIntArray("AndroidScore", extractArrayList(androidScoreRecord));
			intent.putExtras(bundle);

			Log.i("MyScore", Arrays.toString(extractArrayList(myScoreRecord)));
			Log.i("AndroidScore", Arrays.toString(extractArrayList(androidScoreRecord)));
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveSVM() {
		String output = Environment.getExternalStorageDirectory().getPath();
		String dir = output + "/rock-paper-scissors";
		File dataDir = new File(dir);
		if(dataDir.mkdir()) {
			Log.i("Saving SVM:", "Directory created");
		} else {
			Log.i("Saving SVM:", "Data Directory already exist");
		}


	}

	@Override
	protected void onStop() {
		super.onStop();
		saveSVM();
	}
}
