package com.example.emily.dndtimer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    Button btnAdd, btnStart;
    EditText playerName, playerInitiative;

    ArrayList<Player> playerList = new ArrayList<>();
    PlayerListArrayAdapter myAdapter;

    int editPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.player_list);
        btnAdd = findViewById(R.id.btn_add);
        btnStart = findViewById(R.id.btn_start);
        playerName = findViewById(R.id.edit_name);
        playerInitiative = findViewById(R.id.edit_init);

        btnAdd.setOnClickListener(btnAddListener);
        btnStart.setOnClickListener(btnStartListener);
        lv.setOnItemClickListener(itemClickListener);

        playerList = getIntent().getParcelableArrayListExtra(Constants.KEY_PLAYERS);
        myAdapter = new PlayerListArrayAdapter(this, R.layout.item_row_main, playerList);
        lv.setAdapter(myAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePlayerList();
    }

    private void savePlayerList() {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(Constants.FILE_PLAYERS, Context.MODE_PRIVATE);
            StringBuilder sb = new StringBuilder();
            for (Player p : playerList) {
                sb.append(p.getName()).append(",")
                        .append(p.getInitiative()).append(",")
                        .append(p.isAlive()).append("\n");
            }
            outputStream.write(sb.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.d(MainActivity.class.getSimpleName(), "Exception e" + e.toString());
            e.printStackTrace();
        }
    }

    private Player createPlayer(String name, String initiative) {
        int init = Integer.parseInt(initiative);
        return new Player(name, init);
    }

    private boolean validatePlayerInfo() {
        return !playerName.getText().toString().equals("")
                && !playerInitiative.getText().toString().equals("");
    }

    private View.OnClickListener btnAddListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (validatePlayerInfo()) {
                playerList.add(createPlayer(
                        playerName.getText().toString(),
                        playerInitiative.getText().toString()));
                myAdapter.notifyDataSetChanged();
                playerName.setText(null);
                playerInitiative.setText(null);
            } else {
                Toast.makeText(getBaseContext(), "missing player info", Toast.LENGTH_LONG).show();
            }
        }
    };

    private View.OnClickListener btnStartListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (playerList.isEmpty()) {
                Toast.makeText(getBaseContext(), "no players!", Toast.LENGTH_LONG).show();
            } else {
                Intent i = new Intent(MainActivity.this, TimerActivity.class);
                i.putExtra(Constants.KEY_PLAYERS, playerList);
                startActivity(i);
            }
        }
    };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Player selectedPlayer = playerList.get(position);
            editPosition = position;

            Intent i = new Intent(MainActivity.this, EditPlayerActivity.class);
            i.putExtra(Constants.KEY_NAME, selectedPlayer.getName());
            i.putExtra(Constants.KEY_INIT, selectedPlayer.getInitiative());
            startActivityForResult(i, Constants.EDIT_ACTIVITY);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.EDIT_ACTIVITY && resultCode == RESULT_OK && editPosition >= 0) {
            String name = data.getStringExtra(Constants.KEY_NAME);
            int initiative = data.getIntExtra(Constants.KEY_INIT, 0);
            Player p = playerList.get(editPosition);
            p.setName(name);
            p.setInitiative(initiative);
            myAdapter.notifyDataSetChanged();
        }
    }
}
