package persistence;

import model.RosterItem;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* adapted from CPSC 210 JsonSerializationDemo at https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo */

public class JsonTest {
    protected void checkItem(String name, int width, int height, RosterItem item) {
        assertEquals(name, item.getName());
        assertEquals(width, item.getWidth());
        assertEquals(height, item.getHeight());
    }
}
