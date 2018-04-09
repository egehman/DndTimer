package com.example.emily.dndtimer;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jkeaton on 4/6/2018.
 */

public class TimerActivity extends AppCompatActivity implements View.OnClickListener {

    static final String TIME = "1:00";
    static final long INITIAL_TIME = 60000; // 1 Minute
    static final long INTERVAL = 1000; //1 Second
    static final int INITIAL_TIME_S = 60; //Initial time in seconds

    private TextView txtPlayerName;
    //private TextView txtNextPlayer;
    private ListView lstPlayerList;
    private ListView lstGraveyard;
    private TextView txtCountdownTimer;
    private Button btnStart;
    private Button btnPause;
    private Button btnResume;
    private Button btnSkip;
    private Button btnDied;
    private ProgressBar pgbCountdownBar;

    private ArrayList<String> playerList;
    private ArrayList<String> graveyardList;
    private String currentPlayer;
    private boolean playerDied;
    private ArrayAdapter<String> playerListAdapter;
    private ArrayAdapter<String> graveyardListAdapter;
    //Timer Variables
    private boolean isPaused;
    private boolean isCancelled;
    private long timeRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        txtPlayerName = findViewById(R.id.txtPlayerName);
        lstPlayerList = findViewById(R.id.lstNextPlayer);
        txtCountdownTimer = findViewById(R.id.txtCountdownTimer);
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnResume = findViewById(R.id.btnResume);
        btnSkip = findViewById(R.id.btnSkip);
        btnDied = findViewById(R.id.btnDead);
        pgbCountdownBar = findViewById(R.id.pgbCountdownBar);

        playerList = new ArrayList<>();
        graveyardList = new ArrayList<>();
        //Add Players for testing
        playerList.add("Chuck Norris");
        playerList.add("Bruce Wayne");
        playerList.add("Tony Stark");
        playerList.add("Charles Xavier");
        //playerList.addAll(getIntent().getStringArrayListExtra("PlayerList"));
        //txtNextPlayer.setText(playerList.get(1));
        playerListAdapter = new ArrayAdapter<>(this, R.layout.timer_list_text, playerList);
        lstPlayerList.setAdapter(playerListAdapter);

        graveyardListAdapter = new ArrayAdapter<>(this, R.layout.timer_list_text, graveyardList);
        lstPlayerList.setAdapter(graveyardListAdapter);

        SetNextPlayer();

        btnStart.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.INVISIBLE);
        btnResume.setVisibility(View.INVISIBLE);
        txtCountdownTimer.setText(TIME);

        isPaused = true;
        isCancelled = false;
        playerDied = false;

        btnStart.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnResume.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        btnDied.setOnClickListener(this);

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
            case R.id.btnDead:
                playerDied = true;
                SetNextPlayer();
                break;
            default:
                Log.d("TimerActivity", "Onclick fell into default case");
        }
    }

    private void StartTimer() {
        Log.d("TimerActivity", "Starting Timer");

        isPaused = false;
        isCancelled = false;

        new CountDownTimer(INITIAL_TIME, INTERVAL) {
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

        new CountDownTimer(initialTime, INTERVAL) {
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

        }.start();

        isPaused = false;
    }

    private void ResetTimer() {
        Log.d("TimerActivity", "Resetting Timer");

        txtCountdownTimer.setText(TIME);
        SetNextPlayer();

        isCancelled = true;
    }

    private void UpdateTimer(long millisUntilFinished) {

        int secondsRemaining = (int)(millisUntilFinished / INTERVAL);
        String timeRemaining = secondsRemaining / 60 + ":" + checkDigit(secondsRemaining % 60);
        txtCountdownTimer.setText(timeRemaining);
        pgbCountdownBar.setProgress((secondsRemaining * 100)/INITIAL_TIME_S);

        if(secondsRemaining <= 10) pgbCountdownBar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private String checkDigit(long number) {
        return number < 10 ? "0" + number : String.valueOf(number);
    }

    private void SetNextPlayer() {

        if(currentPlayer != null && !currentPlayer.equals("") && !playerDied) playerList.add(currentPlayer);

        if(playerDied) {
            graveyardList.add(currentPlayer);
            playerDied = false;
        }

        currentPlayer = playerList.get(0);
        txtPlayerName.setText(currentPlayer);
        playerList.remove(currentPlayer);
        playerListAdapter.notifyDataSetChanged();
        graveyardListAdapter.notifyDataSetChanged();

    }
}