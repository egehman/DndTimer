package com.example.emily.dndtimer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    Button btnAdd, btnStart;
    EditText playerName, playerInitiative;

    public static final String KEY_NAME = "NAME";
    public static final String KEY_INIT = "INIT";

    static String[] from = {KEY_NAME, KEY_INIT};
    static int[] to = {R.id.player_name, R.id.player_init};

    SimpleAdapter myAdapter;

    ArrayList<HashMap<String, String>> playerList = new ArrayList<>();

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

        myAdapter = new SimpleAdapter(this, playerList, R.layout.item_row_main, from, to);
        lv.setAdapter(myAdapter);
    }

    private HashMap<String, String> createPlayer(String name, String initiative) {
        HashMap<String, String> person = new HashMap<>();
        person.put(KEY_NAME, name);
        person.put(KEY_INIT, initiative);
        return person;
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
            Toast.makeText(getBaseContext(),
                    ((Button) view).getText() + " was clicked!",
                    Toast.LENGTH_LONG).show();
        }
    };
}
