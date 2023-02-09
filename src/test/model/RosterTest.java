package model;

import com.icafe4j.image.gif.GIFFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RosterTest {
    Roster roster;
    RosterItem ri1 = new RosterItem(new BufferedImage(100, 100, 1), "test1.png");
    RosterItem ri2 = new RosterItem(new BufferedImage(1920, 1080, 1), "test2.jpg");
    RosterItem ri3 = new RosterItem(new BufferedImage(16, 9, 1), "Test3.BMP");

    @BeforeEach
    public void runBefore() {
        roster = new Roster();
    }

    @Test
    public void constructorTest() {
        assertTrue(roster.isEmpty());
        assertEquals(0, roster.size());

        assertTrue(roster.getNames().isEmpty());
        assertEquals(0, roster.getNames().size());
    }

    @Test
    public void addTest() {
        roster.add(ri1);

        assertFalse(roster.isEmpty());
        assertEquals(1, roster.size());
        assertEquals(ri1, roster.getItem(0));

        assertFalse(roster.getNames().isEmpty());
        assertEquals(1, roster.getNames().size());
        assertTrue(roster.containsName(ri1.getName()));
    }
    @Test
    public void addTestMultipleTimes() {
        roster.add(ri1);
        roster.add(ri2);

        assertFalse(roster.isEmpty());
        assertEquals(2, roster.size());
        assertEquals(ri1, roster.getItem(0));
        assertEquals(ri2, roster.getItem(1));

        assertFalse(roster.getNames().isEmpty());
        assertEquals(2, roster.getNames().size());
        assertTrue(roster.containsName(ri1.getName()));
        assertTrue(roster.containsName(ri2.getName()));
    }

    @Test
    public void removeTest() {
        roster.add(ri1);
        roster.remove(0);

        assertTrue(roster.isEmpty());
        assertEquals(0, roster.size());

        assertTrue(roster.getNames().isEmpty());
        assertEquals(0, roster.getNames().size());
    }
    @Test
    public void removeTestOutOfBounds() {
        roster.add(ri1);

        assertThrows(IndexOutOfBoundsException.class, () -> roster.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> roster.remove(1));

        assertFalse(roster.isEmpty());
        assertEquals(ri1, roster.getItem(0));
        assertEquals(1, roster.size());

        assertFalse(roster.getNames().isEmpty());
        assertEquals(1, roster.getNames().size());
        assertTrue(roster.containsName(ri1.getName()));
    }
    @Test
    public void removeTestMultipleTimes() {
        roster.add(ri1);
        roster.add(ri2);
        roster.add(ri3);
        roster.remove(0);
        roster.remove(1);

        assertFalse(roster.isEmpty());
        assertEquals(1, roster.size());
        assertEquals(ri2, roster.getItem(0));

        assertFalse(roster.getNames().isEmpty());
        assertEquals(1, roster.getNames().size());
        assertFalse(roster.containsName(ri1.getName()));
        assertTrue(roster.containsName(ri2.getName()));
        assertFalse(roster.containsName(ri3.getName()));
    }

    @Test
    public void clearTest() {
        roster.add(ri1);
        roster.add(ri2);
        roster.clear();

        assertTrue(roster.isEmpty());
        assertEquals(0, roster.size());

        assertTrue(roster.getNames().isEmpty());
        assertEquals(0, roster.getNames().size());
    }

    @Test
    public void isEmptyTest() {
        assertTrue(roster.isEmpty());
        roster.add(ri1);
        assertFalse(roster.isEmpty());
        roster.add(ri2);
        assertFalse(roster.isEmpty());
        roster.remove(0);
        assertFalse(roster.isEmpty());
        roster.clear();
        assertTrue(roster.isEmpty());
    }

    @Test
    public void sizeTest() {
        assertEquals(0, roster.size());
        roster.add(ri1);
        assertEquals(1, roster.size());
        roster.add(ri2);
        assertEquals(2, roster.size());
        roster.remove(0);
        assertEquals(1, roster.size());
        roster.clear();
        assertEquals(0, roster.size());
    }

    @Test
    public void getItemTest() {
        roster.add(ri2);
        roster.add(ri1);

        assertEquals(ri2, roster.getItem(0));
        assertEquals(ri1, roster.getItem(1));
    }

    @Test
    public void getItemsTestOutOfBounds() {
        roster.add(ri2);
        roster.add(ri1);

        assertThrows(IndexOutOfBoundsException.class, () -> roster.getItem(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> roster.getItem(2));
    }

    @Test
    public void getItemsTest() {
        roster.add(ri2);
        roster.add(ri1);
        ArrayList<RosterItem> items = roster.getItems();

        assertEquals(ri2, items.get(0));
        assertEquals(ri1, items.get(1));
    }

    @Test
    public void getFramesTest() {
        roster.add(ri2);
        roster.add(ri1);
        GIFFrame[] frames = roster.getFrames();

        assertEquals(ri2.getFrame(), frames[0]);
        assertEquals(ri1.getFrame(), frames[1]);
    }

    @Test
    public void swapTest() {
        roster.add(ri1);
        roster.add(ri2);
        roster.add(ri3);

        roster.swap(0, 1);

        assertEquals(ri2, roster.getItem(0));
        assertEquals(ri1, roster.getItem(1));
        assertEquals(ri3, roster.getItem(2));

        roster.swap(2, 0);

        assertEquals(ri3, roster.getItem(0));
        assertEquals(ri1, roster.getItem(1));
        assertEquals(ri2, roster.getItem(2));
    }

    @Test
    public void swapTestOutOfBounds() {
        roster.add(ri1);
        roster.add(ri2);
        roster.add(ri3);

        assertThrows(IndexOutOfBoundsException.class, () -> roster.swap(-1, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> roster.swap(1, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> roster.swap(42, -19));

        assertEquals(ri1, roster.getItem(0));
        assertEquals(ri2, roster.getItem(1));
        assertEquals(ri3, roster.getItem(2));
    }

    @Test
    public void shiftTest() {
        roster.add(ri1);
        roster.add(ri2);
        roster.add(ri3);

        roster.shift(0, 1);

        assertEquals(ri2, roster.getItem(0));
        assertEquals(ri1, roster.getItem(1));
        assertEquals(ri3, roster.getItem(2));

        roster.shift(2, 0);

        assertEquals(ri3, roster.getItem(0));
        assertEquals(ri2, roster.getItem(1));
        assertEquals(ri1, roster.getItem(2));

        roster.shift(0, 2);

        assertEquals(ri2, roster.getItem(0));
        assertEquals(ri1, roster.getItem(1));
        assertEquals(ri3, roster.getItem(2));
    }

    @Test
    public void shiftTestOutOfBounds() {
        roster.add(ri1);
        roster.add(ri2);
        roster.add(ri3);

        assertThrows(IndexOutOfBoundsException.class, () -> roster.shift(-1, 2));
        assertThrows(IndexOutOfBoundsException.class, () -> roster.shift(1, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> roster.shift(42, -19));


        assertEquals(ri1, roster.getItem(0));
        assertEquals(ri2, roster.getItem(1));
        assertEquals(ri3, roster.getItem(2));
    }

    @Test
    public void containsNameTest() {
        roster.add(ri1);
        roster.add(ri2);
        roster.add(ri3);

        assertTrue(roster.containsName(ri1.getName()));
        assertTrue(roster.containsName(ri2.getName().toLowerCase()));
        assertTrue(roster.containsName(ri3.getName().toUpperCase()));

        assertFalse(roster.containsName(" " + ri1.getName()));
        assertFalse(roster.containsName("._." + ri2.getName()));
        assertFalse(roster.containsName("T.T" + ri3.getName()));
    }

    @Test
    public void renameTest() {
        roster.add(ri1);
        roster.add(ri2);
        roster.add(ri3);

        String name = "new.png";

        roster.rename(ri1.getName(), name);

        assertFalse(roster.containsName(ri1.getName()));
        assertTrue(roster.containsName(ri2.getName()));
        assertTrue(roster.containsName(ri3.getName()));
        assertTrue(roster.containsName(name));

        roster.rename(ri2.getName(), ri1.getName());

        assertTrue(roster.containsName(ri1.getName()));
        assertFalse(roster.containsName(ri2.getName()));
        assertTrue(roster.containsName(ri3.getName()));
        assertTrue(roster.containsName(name));
    }
}