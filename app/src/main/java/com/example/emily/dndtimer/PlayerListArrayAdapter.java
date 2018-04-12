package com.example.emily.dndtimer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class PlayerListArrayAdapter extends ArrayAdapter<Player> {

    public PlayerListArrayAdapter(@NonNull Context context, int resource, ArrayList<Player> players) {
        super(context, resource, players);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        Player p = getItem(position);
        ViewHolder holder;

        if (v == null) {
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.item_row_main, null);
            holder = new ViewHolder();
            holder.playerName = v.findViewById(R.id.player_name);
            holder.playerInit = v.findViewById(R.id.player_init);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        if (p != null && holder.playerName != null) {
            holder.playerName.setText(p.getName());
        }

        if (p != null && holder.playerInit != null) {
            holder.playerInit.setText(String.format(Locale.US, "%d", p.getInitiative()));
        }

        return v;
    }

    static class ViewHolder {
        TextView playerName;
        TextView playerInit;
    }
}
