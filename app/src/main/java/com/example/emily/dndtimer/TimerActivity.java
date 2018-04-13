package com.example.emily.dndtimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.Math.min;

public class TimerActivity extends AppCompatActivity {

    //region Constants
    static final String TIME = "1:00";
    static final String TIME_UP = "0:00";
    static final long INITIAL_TIME = 60000; // 1 Minute
    static final long INTERVAL = 1000; //1 Second
    //endregion

    //region Global Variables
    //preference settings
    private long initialTime;

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

    private ArrayList<Player> playerList;
    private ArrayList<Player> graveyardList;
    private ArrayList<Player> filteredPlayerList;
    private ArrayList<Player> filteredGraveyardList;
    private Player currentPlayer;
    private PlayerListArrayAdapter playerListAdapter;
    private PlayerListArrayAdapter graveyardListAdapter;
    //Timer Variables
    private boolean isPaused = true;
    private boolean isCancelled = false;
    private long timeRemaining;

    private int playerListSetting;
    private int graveyardListSetting;
//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        initializeViews();
        initializeLists();

        SetNextPlayer();
    }

    @Override
    protected void onResume() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int timerStyle = Integer.parseInt(prefs.getString("timer_style", "0"));
        initialTime = Long.parseLong(prefs.getString("timer_time", "60000"));
        UpdateTimer(initialTime);

        playerListSetting = Integer.parseInt(prefs.getString("upcoming_players", "0"));
        graveyardListSetting = Integer.parseInt(prefs.getString("graveyard_players", "0"));

        setTimerStyle(timerStyle);
        setFilteredGraveyardList();
        setFilteredPlayerList();

        super.onResume();
    }

    @Override
    protected void onPause() {
        PauseTimer();
        super.onPause();
    }

    //region Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                GoToSettings();
                return (true);

        }
        return (super.onOptionsItemSelected(item));
    }

    private void GoToSettings()
    {
        Intent i = new Intent(this, DnDPreferenceActivity.class);
        startActivity(i);
    }

    //endregion

    //region Listeners
    private View.OnClickListener btnPlayerDeathListener = new View.OnClickListener() {
        public void onClick(View view) {
            currentPlayer.setAlive(false);
            ResetTimer();
            SetNextPlayer();
        }
    };

    private View.OnClickListener btnStartTimerListener = new View.OnClickListener() {
        public void onClick(View view) {
            btnPause.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.INVISIBLE);

            StartTimer();
        }
    };

    private View.OnClickListener btnPauseTimerListener = new View.OnClickListener() {
        public void onClick(View view) {
            btnResume.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.INVISIBLE);
            PauseTimer();
        }
    };

    private View.OnClickListener btnResumeTimerListener = new View.OnClickListener() {
        public void onClick(View view) {
            btnPause.setVisibility(View.VISIBLE);
            btnResume.setVisibility(View.INVISIBLE);

            ResumeTimer();
        }
    };

    private View.OnClickListener btnNextPlayerListener = new View.OnClickListener() {
        public void onClick(View view) {
            btnPause.setVisibility(View.INVISIBLE);
            btnResume.setVisibility(View.INVISIBLE);
            btnStart.setVisibility(View.VISIBLE);

            ResetTimer();
            SetNextPlayer();
        }
    };

//endregion

    //region timer methods
    private void StartTimer() {
        Log.d("TimerActivity", "Starting Timer");

        isPaused = false;
        isCancelled = false;

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

        UpdateTimer(initialTime);

        isCancelled = true;
    }

    private void UpdateTimer(long millisUntilFinished) {

        int secondsRemaining = (int) (millisUntilFinished / INTERVAL);
        String timeRemaining = secondsRemaining / 60 + ":" + checkDigit(secondsRemaining % 60);

        if (txtCountdownTimer.getVisibility() == View.VISIBLE) {
            txtCountdownTimer.setText(timeRemaining);

            if (secondsRemaining <= 10)
                txtCountdownTimer.setTextColor(Color.RED);
            else
                txtCountdownTimer.setTextColor(Color.GRAY);
        }

        if (pgbCountdownBar.getVisibility() == View.VISIBLE) {
            float fraction = millisUntilFinished / (float) initialTime;
            int countdownValue = (int) (fraction * 100);
            Log.d("CountdownTimer", "Remaining Time: " + countdownValue);
            pgbCountdownBar.setProgress(0); //do this for some reason
            pgbCountdownBar.setMax(0); //do this for some reason
            pgbCountdownBar.setMax(100); //do this for some reason
            pgbCountdownBar.animate();
            pgbCountdownBar.setProgress(countdownValue);

            if (secondsRemaining <= 10)
                pgbCountdownBar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.MULTIPLY);
//            else
            //pgbCountdownBar.getProgressDrawable().setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    private String checkDigit(long number) {
        return number < 10 ? "0" + number : String.valueOf(number);
    }
//endregion

    //region setting/style methods
    private void setTimerStyle(int timerStyle) {
        switch (timerStyle) {
            case 0: //We want both countdown timer and progress bar
                txtCountdownTimer.setVisibility(View.VISIBLE);
                pgbCountdownBar.setVisibility(View.VISIBLE);
                break;

            case 1: //We only want Countdown Timer
                txtCountdownTimer.setVisibility(View.VISIBLE);
                pgbCountdownBar.setVisibility(View.INVISIBLE);
                break;

            case 2: //we only want Progress bar
                txtCountdownTimer.setVisibility(View.INVISIBLE);
                pgbCountdownBar.setVisibility(View.VISIBLE);
                break;

            default:
                Log.d("TimerActivity", "Unknown timer style");
        }
    }

    //There may be a better/cleaner way to do this
    private void setFilteredPlayerList() {

        switch (playerListSetting) {
            case 0: //We want to see all players
                filteredPlayerList.clear();
                if (playerList != null && !playerList.isEmpty())
                    filteredPlayerList.addAll(playerList);
                break;

            case 1: //We only want next player
                filteredPlayerList.clear();
                if (playerList != null && !playerList.isEmpty())
                    filteredPlayerList.add(playerList.get(0));
                break;

            case 2: //we only want next 2 players
                filteredPlayerList.clear();
                if (playerList != null && !playerList.isEmpty())
                    filteredPlayerList.addAll(playerList.subList(0, min(playerList.size(), 2)));
                break;

            case 5: //we only want next 5 players
                filteredPlayerList.clear();
                if (playerList != null && !playerList.isEmpty())
                    filteredPlayerList.addAll(playerList.subList(0, min(playerList.size(), 5)));
                break;

            default:
                Log.d("TimerActivity", "Unknown number of upcoming players");
        }

        playerListAdapter.notifyDataSetChanged();
    }

    private void setFilteredGraveyardList() {
        switch (graveyardListSetting) {
            case 0: //We want to see all players
                filteredGraveyardList.clear();
                if (graveyardList != null && !graveyardList.isEmpty())
                    filteredGraveyardList.addAll(graveyardList);
                break;

            case 1: //We only want 1 player
                filteredGraveyardList.clear();
                if (graveyardList != null && !graveyardList.isEmpty())
                    filteredGraveyardList.add(graveyardList.get(0));
                break;

            case 2: //we only want 2 players
                filteredGraveyardList.clear();
                if (graveyardList != null && !graveyardList.isEmpty())
                    filteredGraveyardList.addAll(graveyardList.subList(0, min(graveyardList.size(), 2)));
                break;

            case 5: //we only want 5 players
                filteredGraveyardList.clear();
                if (graveyardList != null && !graveyardList.isEmpty())
                    filteredGraveyardList.addAll(graveyardList.subList(0, min(graveyardList.size(), 5)));
                break;

            default:
                Log.d("TimerActivity", "Unknown number of upcoming players");
        }

        graveyardListAdapter.notifyDataSetChanged();
    }
    //endregion

    //region Initialization Methods
    private void initializeViews() {
        txtPlayerName = findViewById(R.id.txtPlayerName);
        lstPlayerList = findViewById(R.id.lstNextPlayer);
        lstGraveyard = findViewById(R.id.lstGraveyard);
        txtCountdownTimer = findViewById(R.id.txtCountdownTimer);
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnResume = findViewById(R.id.btnResume);
        btnSkip = findViewById(R.id.btnSkip);
        btnDied = findViewById(R.id.btnDead);
        pgbCountdownBar = findViewById(R.id.pgbCountdownBar);
        pgbCountdownBar.setProgress(100);
        //pgbCountdownBar.getProgressDrawable().setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.MULTIPLY);

        //set initial visibilities
        btnStart.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.INVISIBLE);
        btnResume.setVisibility(View.INVISIBLE);
        txtCountdownTimer.setText(TIME);

        //Set button Listeners
        btnStart.setOnClickListener(btnStartTimerListener);
        btnPause.setOnClickListener(btnPauseTimerListener);
        btnResume.setOnClickListener(btnResumeTimerListener);
        btnSkip.setOnClickListener(btnNextPlayerListener);
        btnDied.setOnClickListener(btnPlayerDeathListener);
    }

    private void initializeLists() {
        playerList = new ArrayList<>();
        graveyardList = new ArrayList<>();
        filteredPlayerList = new ArrayList<>();
        filteredGraveyardList = new ArrayList<>();
        playerList = getIntent().getParcelableArrayListExtra(Constants.KEY_PLAYERS);

        //set adapters
        playerListAdapter = new PlayerListArrayAdapter(this, R.layout.item_row_timer, filteredPlayerList);
        lstPlayerList.setAdapter(playerListAdapter);

        graveyardListAdapter = new PlayerListArrayAdapter(this, R.layout.item_row_timer, filteredGraveyardList);
        lstGraveyard.setAdapter(graveyardListAdapter);
    }
    //endregion

    private void SetNextPlayer() {

        if (currentPlayer != null && currentPlayer.isAlive())
            playerList.add(currentPlayer);

        if (currentPlayer != null && !currentPlayer.isAlive()) {
            graveyardList.add(currentPlayer);
        }

        if (!playerList.isEmpty()) {
            currentPlayer = playerList.get(0);
            txtPlayerName.setText(currentPlayer.getName());
            playerList.remove(currentPlayer);
        } else {
            //Game Over
            GameOver();
        }

        setFilteredPlayerList();
        setFilteredGraveyardList();
    }

    private void GameOver() {
        Toast.makeText(getBaseContext(), "Everyone Died, Game Over", Toast.LENGTH_LONG).show();
        //hide all buttons
        btnStart.setVisibility(View.INVISIBLE);
        btnPause.setVisibility(View.INVISIBLE);
        btnResume.setVisibility(View.INVISIBLE);
        btnDied.setVisibility(View.INVISIBLE);
        btnSkip.setVisibility(View.INVISIBLE);
        txtCountdownTimer.setText(TIME_UP);
        txtCountdownTimer.setTextColor(Color.RED);
        pgbCountdownBar.setProgress(0);
        pgbCountdownBar.setProgress(0);
        finish();
    }
}
