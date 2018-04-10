package com.example.emily.dndtimer;

public class Player {

    private String name;
    private int initiative;
    private boolean isAlive;

    public Player(String name, int initiative) {
        this.name = name;
        this.initiative = initiative;
        this.isAlive = true; //New players are always alive
    }

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
}
