package com.example.emily.dndtimer;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Player implements Parcelable, Comparable<Player> {

    private String name;
    private int initiative;
    private boolean isAlive;

    public Player(String name, int initiative) {
        this.name = name;
        this.initiative = initiative;
        this.isAlive = true; //New players are always alive
    }

    public Player(String name, int initiative, boolean isAlive) {
        this.name = name;
        this.initiative = initiative;
        this.isAlive = isAlive;
    }

    protected Player(Parcel in) {
        name = in.readString();
        initiative = in.readInt();
        isAlive = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(initiative);
        dest.writeByte((byte) (isAlive ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInitiative() {
        return initiative;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    //This compareTo is designed to sort a list of players in descending order by initiative ie 20, 19, 18...
    @Override
    public int compareTo(@NonNull Player o) {
        return this.initiative > o.initiative ? -1 :
                this.initiative < o.initiative ? 1 : 0;
    }
}
