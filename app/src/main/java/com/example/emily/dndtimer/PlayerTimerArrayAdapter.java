package com.example.emily.dndtimer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayerTimerArrayAdapter extends ArrayAdapter<Player> {

    public PlayerTimerArrayAdapter(@NonNull Context context, int resource, ArrayList<Player> players) {
        super(context, resource, players);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            LayoutInflater li = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.timer_list_text, null);
            holder = new ViewHolder();
            holder.playerName = v.findViewById(R.id.playerName);
            v.setTag(holder);
        } else {

            holder = (ViewHolder) v.getTag();
        }

        Player p = getItem(position);

        Log.d("PlayerTimerAdapter", p.toString());

        if (p.getName() != null) holder.playerName.setText(p.getName());

        return v;
    }

    static class ViewHolder {
        TextView playerName;
    }
}
