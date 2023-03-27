package model;

import com.icafe4j.image.gif.GIFFrame;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

// Represents the collection of frames comprising the output gif
public class Roster implements Writable {
    ArrayList<RosterItem> items;
    HashSet<String> names;

    EventLog log;

    public Roster() {
        items = new ArrayList<>();
        names = new HashSet<>();
        log = EventLog.getInstance();

        log.logEvent(new Event("Roster created"));
    }

    // MODIFIES: this
    // EFFECTS: adds ri to the roster and its name to the name set
    public void add(RosterItem ri) {
        items.add(ri);
        names.add(ri.getName().toLowerCase());

        log.logEvent(new Event("Added " + ri.getName() + " to the roster"));
    }

    // REQUIRES: 0 <= i < size();
    // MODIFIES: this
    // EFFECTS: removes the RosterItem at index i from the roster and its name from the name set
    public void remove(int i) {
        String name = items.get(i).getName();

        names.remove(name.toLowerCase());
        items.remove(i);

        log.logEvent(new Event("Removed " + name + " from the roster"));
    }

    // MODIFIES: this
    // EFFECTS: clears the roster and the name set
    public void clear() {
        items.clear();
        names.clear();

        log.logEvent(new Event("Cleared the roster"));
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

    // EFFECTS: returns the name set
    public HashSet<String> getNames() {
        return names;
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

        log.logEvent(new Event("Swapped " + rl.getName() + " and " + rs.getName()));
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

        log.logEvent(new Event("Shifted " + r.getName() + " from index " + a + " to index " + b));
    }

    // EFFECTS: returns true if i is out of roster bounds
    private boolean outOfBounds(int i) {
        return 0 > i || i >= size();
    }

    // EFFECTS: returns true if a RosterItem already has name n
    public boolean containsName(String n) {
        return names.contains(n.toLowerCase());
    }

    // REQUIRES: name is already in the name set
    // MODIFIES: this
    // EFFECTS: replaces name in name set with newName
    public void rename(String name, String newName) {
        names.remove(name.toLowerCase());
        names.add(newName.toLowerCase());

        log.logEvent(new Event("Renamed " + name + " to " + newName));
    }

    // EFFECTS: returns JSON representation of the roster
    @Override
    public JSONObject toJson() throws IOException {
        JSONObject json = new JSONObject();
        json.put("items", itemsToJson());
        return json;
    }

    // EFFECTS: returns roster items as a JSON array
    private JSONArray itemsToJson() throws IOException {
        JSONArray jsonArray = new JSONArray();

        for (RosterItem i : items) {
            jsonArray.put(i.toJson());
        }

        return jsonArray;
    }
}
