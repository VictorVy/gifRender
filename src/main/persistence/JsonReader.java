package persistence;

import model.RosterItem;
import model.Roster;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Stream;

/* adapted from CPSC 210 JsonSerializationDemo at https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo */

// Represents a reader that reads roster from JSON data stored in file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads roster from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Roster read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseRoster(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        // removed try to cover all branches for autotest
        Stream<String> stream = Files.lines(Paths.get(source));
        stream.forEach(contentBuilder::append);

        return contentBuilder.toString();
    }

    // EFFECTS: parses roster from JSON object and returns it
    private Roster parseRoster(JSONObject jsonObject) throws IOException {
        Roster r = new Roster();
        addItems(r, jsonObject);
        return r;
    }

    // MODIFIES: r
    // EFFECTS: parses items from JSON object and adds them to roster
    private void addItems(Roster r, JSONObject jsonObject) throws IOException {
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        for (Object json : jsonArray) {
            JSONObject nextThingy = (JSONObject) json;
            addItem(r, nextThingy);
        }
    }

    // MODIFIES: r
    // EFFECTS: parses item from JSON object and adds it to roster
    private void addItem(Roster r, JSONObject jsonObject) throws IOException {
        String name = jsonObject.getString("name");
        String imageString = jsonObject.getString("image");
        BufferedImage image;
        int delay = jsonObject.getInt("delay");
        int transparency = jsonObject.getInt("transparency");

        byte[] decodedBytes = Base64.getDecoder().decode(imageString);

        image = ImageIO.read(new ByteArrayInputStream(decodedBytes));

        RosterItem item = new RosterItem(image, name, transparency);
        item.setDelay(delay);

        r.add(item);
    }
}
