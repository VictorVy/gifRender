package persistence;

import model.RosterItem;
import model.Roster;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/* adapted from CPSC 210 JsonSerializationDemo at https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo */

class JsonReaderTest extends JsonTest {
    RosterItem ri1 = new RosterItem(new BufferedImage(100, 100, 1), "test1.png");
    RosterItem ri2 = new RosterItem(new BufferedImage(1920, 1080, 1), "test2.jpg");

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/noSuchFile.json");
        try {
            Roster r = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyWorkRoom() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyRoster.json");
        try {
            Roster r = reader.read();
            assertEquals(0, r.size());
            assertTrue(r.isEmpty());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralWorkRoom() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralRoster.json");
        try {
            Roster r = reader.read();

            List<RosterItem> items = r.getItems();
            assertEquals(2, items.size());
            checkItem(ri1.getName(), ri1.getWidth(), ri1.getHeight(), items.get(0));
            checkItem(ri2.getName(), ri2.getWidth(), ri2.getHeight(), items.get(1));
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}