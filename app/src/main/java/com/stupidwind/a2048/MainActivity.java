package com.stupidwind.a2048;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tv_score;
    private TextView tv_high_score;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_score = (TextView) findViewById(R.id.tv_score);
        tv_high_score = (TextView) findViewById(R.id.tv_high_score);
        gameView = (GameView) findViewById(R.id.gameview);

        gameView.setShowScoreListener(new GameView.ShowScoreListener() {
            @Override
            public void showScore() {
                tv_score.setText("SCORE: " + gameView.getScore());
            }

            @Override
            public void updateHighScore() {
                tv_high_score.setText("HIGH SCORE: " + gameView.getHighScore());
            }
        });
    }

}
