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

    // REQUIRES: 0 <= a < size() and 0 <= b < size()
    // MODIFIES: this
    // MODIFIES: swaps the RosterItems and index a and index b
    public void swap(int a, int b) {
        if (outOfBounds(a) || outOfBounds(b)) {
            throw new IndexOutOfBoundsException();
        }

        int l = Math.max(a, b);
        int s = Math.min(a, b);

        RosterItem rl = items.remove(l);
        RosterItem rs = items.remove(s);

        items.add(s, rl);
        items.add(l, rs);
    }

    // REQUIRES: 0 <= a < size() and 0 <= b < size()
    // MODIFIES: this
    // EFFECTS: shifts the RosterItem at index a to index b
    public void shift(int a, int b) {
        if (outOfBounds(a) || outOfBounds(b)) {
            throw new IndexOutOfBoundsException();
        }

        RosterItem r = items.remove(a);
        items.add(b, r);
    }

    // EFFECTS: returns true if i is out of roster bounds
    private boolean outOfBounds(int i) {
        return 0 > i || i >= size();
    }
}
