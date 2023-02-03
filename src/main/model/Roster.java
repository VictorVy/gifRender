package model;

import com.icafe4j.image.gif.GIFFrame;

import java.util.ArrayList;
import java.util.Arrays;

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
        Object[] arr = roster.stream().map(ri -> new GIFFrame(ri.getImage())).toArray();
        //https://stackoverflow.com/questions/12210311/downcasting-of-arrays-in-java
        return Arrays.copyOf(arr, arr.length, GIFFrame[].class);
    }
}
