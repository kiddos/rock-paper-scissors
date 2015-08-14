package com.kiddos.rockpaperscissors;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends Activity {
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
	private ImageView android;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		rock = (ImageButton) findViewById(R.id.ibRock);
		paper = (ImageButton) findViewById(R.id.ibPaper);
		scissors = (ImageButton) findViewById(R.id.ibScissors);
		result = (TextView) findViewById(R.id.tvResult);
		android = (ImageView) findViewById(R.id.ivAndroid);
		myScore = (TextView) findViewById(R.id.tvMe);
		androidScore = (TextView) findViewById(R.id.tvAndroid);

		// events
		ButtonHandler handler = new ButtonHandler();
		rock.setOnTouchListener(handler);
		paper.setOnTouchListener(handler);
		scissors.setOnTouchListener(handler);

		// initialize
		if (myScore.getText().toString().equals("")) {
			myScore.setText("0");
		}
		if (androidScore.getText().toString().equals("")) {
			androidScore.setText("0");
		}
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

		// compute android's random choice
		private int getAndroidChoice() {
			int choice[] = {ROCK, PAPER, SCISSORS};
			Random r = new Random();
			return choice[r.nextInt(choice.length)];
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

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int androidChoice = getAndroidChoice();
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

				// display android's choice
				if (androidChoice == ROCK) {
					android.setImageResource(R.drawable.rock);
				} else if (androidChoice == PAPER) {
					android.setImageResource(R.drawable.paper);
				} else if (androidChoice == SCISSORS) {
					android.setImageResource(R.drawable.scissors);
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
				} else if (a >= limit) {
					MainActivity.this.result.setText("Android\n" + RESULT);
					myScore.setText("0");
					androidScore.setText("0");
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_exit) {
			finish();
			return true;
		} else if (id == R.id.action_score) {
			Intent intent = new Intent(MainActivity.this, Score.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
