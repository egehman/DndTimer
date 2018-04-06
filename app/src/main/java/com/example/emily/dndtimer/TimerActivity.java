package com.example.emily.dndtimer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jkeaton on 4/6/2018.
 */

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {

    static final String time = "1:00";

    private TextView txtPlayerName;
    //private TextView txtNextPlayer;
    private ListView lstPlayerList;
    private TextView txtCountdownTimer;
    private Button btnStart;
    private Button btnPause;
    private Button btnResume;
    private Button btnSkip;

    private ArrayList<String> playerList;
    private String currentPlayer;
    private ArrayAdapter<String> listAdapter;
    //Timer Variables
    private boolean isPaused;
    private boolean isCancelled;
    private long timeRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        txtPlayerName = findViewById(R.id.txtPlayerName);
        //txtNextPlayer = findViewById(R.id.txtNextPlayer);
        lstPlayerList = findViewById(R.id.lstNextPlayer);
        txtCountdownTimer = findViewById(R.id.txtCountdownTimer);
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnResume = findViewById(R.id.btnResume);
        btnSkip = findViewById(R.id.btnSkip);


        playerList = new ArrayList<>();
        //Add Players for testing
        playerList.add("Chuck Norris");
        playerList.add("Bruce Wayne");
        playerList.add("Tony Stark");
        playerList.add("Charles Xavier");
        //playerList.addAll(getIntent().getStringArrayListExtra("PlayerList"));
        //txtNextPlayer.setText(playerList.get(1));
        listAdapter = new ArrayAdapter<>(this, R.layout.timer_list_text, playerList);
        lstPlayerList.setAdapter(listAdapter);

        SetNextPlayer();

        btnStart.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.INVISIBLE);
        btnResume.setVisibility(View.INVISIBLE);
        txtCountdownTimer.setText(time);

        isPaused = true;
        isCancelled = false;

        btnStart.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnResume.setOnClickListener(this);
        btnSkip.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnStart:
                btnPause.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.INVISIBLE);

                StartTimer();
                break;

            case R.id.btnPause:
                btnResume.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.INVISIBLE);

                PauseTimer();
                break;

            case R.id.btnResume:
                btnPause.setVisibility(View.VISIBLE);
                btnResume.setVisibility(View.INVISIBLE);

                ResumeTimer();
                break;

            case R.id.btnSkip:
                btnPause.setVisibility(View.INVISIBLE);
                btnResume.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.VISIBLE);

                ResetTimer();
                break;

            default:
                Log.d("TimerActivity", "Onclick fell into default case");
        }
    }

    private void StartTimer() {
        Log.d("TimerActivity", "Starting Timer");

        isPaused = false;
        isCancelled = false;

        long initialTime = 60000; // 1 minute
        long countDownInterval = 1000; // 1 Second

        CountDownTimer timer = new CountDownTimer(initialTime, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isPaused || isCancelled) {
                    cancel(); //User selected Pause or Skip
                } else {
                    UpdateTimer(millisUntilFinished);
                    timeRemaining = millisUntilFinished;
                }
            }

            @Override
            public void onFinish() {
                btnPause.setEnabled(false);
                ResetTimer();
            }

            private void UpdateTimer(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                String timeRemaining = secondsRemaining / 60 + ":" + checkDigit(secondsRemaining % 60);
                txtCountdownTimer.setText(timeRemaining);
            }

            private String checkDigit(long number) {
                return number < 10 ? "0" + number : String.valueOf(number);
            }

        }.start();

    }

    private void PauseTimer() {
        Log.d("TimerActivity", "Pausing Timer");

        isPaused = true;
    }

    private void ResumeTimer() {
        Log.d("TimerActivity", "Resuming Timer");

        isPaused = false;
        isCancelled = false;

        long initialTime = timeRemaining;
        long countDownInterval = 1000; // 1 Second

        CountDownTimer timer = new CountDownTimer(initialTime, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isPaused || isCancelled) {
                    cancel(); //User selected Pause or Skip
                } else {
                    UpdateTimer(millisUntilFinished);
                    timeRemaining = millisUntilFinished;
                }
            }

            @Override
            public void onFinish() {
                btnPause.setEnabled(false);
            }

            private void UpdateTimer(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                String timeRemaining = secondsRemaining / 60 + ":" + checkDigit(secondsRemaining % 60);
                txtCountdownTimer.setText(timeRemaining);
            }

            private String checkDigit(long number) {
                return number < 10 ? "0" + number : String.valueOf(number);
            }

        }.start();

        isPaused = false;
    }

    private void ResetTimer() {
        Log.d("TimerActivity", "Resetting Timer");

        txtCountdownTimer.setText(time);
        SetNextPlayer();

        isCancelled = true;
    }


    private void SetNextPlayer() {

        if(currentPlayer != null && !currentPlayer.equals("")) playerList.add(currentPlayer);
        currentPlayer = playerList.get(0);
        txtPlayerName.setText(currentPlayer);
        playerList.remove(currentPlayer);
        listAdapter.notifyDataSetChanged();

    }
}