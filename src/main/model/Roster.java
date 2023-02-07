package model;

import com.icafe4j.image.gif.GIFFrame;

import java.util.ArrayList;

// Represents the collection of frames comprising the output gif
public class Roster {
    ArrayList<RosterItem> roster;

    public Roster() {
        roster = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: adds ri to the roster
    public void add(RosterItem ri) {
        roster.add(ri);
    }

    // MODIFIES: this
    // EFFECTS: removes the RosterItem at index i from the roster
    public void remove(int i) {
        roster.remove(i);
    }

    // MODIFIES: this
    // EFFECTS: clears the roster
    public void clear() {
        roster.clear();
    }

    // EFFECTS: returns true if the roster is empty
    public boolean isEmpty() {
        return roster.isEmpty();
    }

    // EFFECTS: returns the size of the roster
    public int size() {
        return roster.size();
    }

    // EFFECTS: returns the RosterItem at index i
    public RosterItem getItem(int i) {
        return roster.get(i);
    }

    // EFFECTS: returns the roster
    public ArrayList<RosterItem> getItems() {
        return roster;
    }

    // EFFECTS: returns the roster as an array of GIFFrames
    public GIFFrame[] getFrames() {
        return ModelUtils.toGifFrames(roster);
    }
}
