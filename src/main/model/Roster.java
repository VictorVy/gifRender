package model;

import com.icafe4j.image.gif.GIFFrame;

import java.util.ArrayList;

// Represents the collection of frames comprising the output gif
public class Roster {
    ArrayList<RosterItem> items;

    public Roster() {
        items = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: adds ri to the roster
    public void add(RosterItem ri) {
        items.add(ri);
    }

    // REQUIRES: 0 <= i < size();
    // MODIFIES: this
    // EFFECTS: removes the RosterItem at index i from the roster
    public void remove(int i) {
        items.remove(i);
    }

    // MODIFIES: this
    // EFFECTS: clears the roster
    public void clear() {
        items.clear();
    }

    // EFFECTS: returns true if the roster is empty
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // EFFECTS: returns the size of the roster
    public int size() {
        return items.size();
    }

    // REQUIRES: 0 <= i < size();
    // EFFECTS: returns the RosterItem at index i
    public RosterItem getItem(int i) {
        return items.get(i);
    }

    // EFFECTS: returns the roster
    public ArrayList<RosterItem> getItems() {
        return items;
    }

    // EFFECTS: returns the roster as an array of GIFFrames
    public GIFFrame[] getFrames() {
        return ModelUtils.toGifFrames(items);
    }
}
