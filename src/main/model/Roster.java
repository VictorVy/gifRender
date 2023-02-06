package model;

import com.icafe4j.image.gif.GIFFrame;

import java.util.ArrayList;
import java.util.Arrays;

// Represents the collection of frames comprising the output gif
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

    public void clear() {
        roster.clear();
    }

    public boolean isEmpty() {
        return roster.isEmpty();
    }

    public int size() {
        return roster.size();
    }

    public RosterItem getItem(int i) {
        return roster.get(i);
    }

    public ArrayList<RosterItem> getItems() {
        return roster;
    }

    public GIFFrame[] getFrames() {
        Object[] arr = roster.stream().map(RosterItem::getFrame).toArray();
        //https://stackoverflow.com/questions/12210311/downcasting-of-arrays-in-java
        return Arrays.copyOf(arr, arr.length, GIFFrame[].class);
    }
}
