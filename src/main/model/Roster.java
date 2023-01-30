package model;

import java.util.ArrayList;

public class Roster {
    ArrayList<RosterItem> roster;

    public Roster() {
        roster = new ArrayList<>();
    }

    public void add(RosterItem ri) {
        roster.add(ri);
    }

    public void remove(int index) {
        roster.remove(index);
    }

    public boolean isEmpty() {
        return roster.isEmpty();
    }
}
