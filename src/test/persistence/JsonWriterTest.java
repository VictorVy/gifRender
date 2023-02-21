package persistence;

import model.RosterItem;
import model.Roster;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/* adapted from CPSC 210 JsonSerializationDemo at https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo */

class JsonWriterTest extends JsonTest {
    //NOTE TO CPSC 210 STUDENTS: the strategy in designing tests for the JsonWriter is to
    //write data to a file and then use the reader to read it back in and check that we
    //read in a copy of what was written out.

    RosterItem ri1 = new RosterItem(new BufferedImage(100, 100, 1), "test1.png");
    RosterItem ri2 = new RosterItem(new BufferedImage(1920, 1080, 1), "test2.jpg");

    @Test
    void testWriterInvalidFile() {
        try {
            Roster r = new Roster();
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyWorkroom() {
        try {
            Roster r = new Roster();
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyRoster.json");
            writer.open();
            writer.write(r);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyRoster.json");
            r = reader.read();
            assertEquals(0, r.size());
            assertTrue(r.isEmpty());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralWorkroom() {
        try {
            Roster r = new Roster();
            r.add(ri1);
            r.add(ri2);
            JsonWriter writer = new JsonWriter("./data/testWriterGeneralRoster.json");
            writer.open();
            writer.write(r);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralRoster.json");
            r = reader.read();

            List<RosterItem> items = r.getItems();
            assertEquals(2, items.size());
            checkItem(ri1.getName(), ri1.getWidth(), ri1.getHeight(), items.get(0));
            checkItem(ri2.getName(), ri2.getWidth(), ri2.getHeight(), items.get(1));
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}