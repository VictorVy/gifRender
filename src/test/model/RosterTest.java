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

    @BeforeEach
    public void runBefore() {
        roster = new Roster();
    }

    @Test
    public void constructorTest() {
        assertTrue(roster.isEmpty());
        assertEquals(0, roster.size());
    }

    @Test
    public void addTest() {
        roster.add(ri1);

        assertFalse(roster.isEmpty());
        assertEquals(1, roster.size());
        assertEquals(ri1, roster.getItem(0));
    }
    @Test
    public void addTestMultipleTimes() {
        roster.add(ri1);
        roster.add(ri2);

        assertFalse(roster.isEmpty());
        assertEquals(2, roster.size());
        assertEquals(ri1, roster.getItem(0));
        assertEquals(ri2, roster.getItem(1));
    }

    @Test
    public void removeTest() {
        roster.add(ri1);
        roster.remove(0);

        assertTrue(roster.isEmpty());
        assertEquals(0, roster.size());
    }
    @Test
    public void removeTestMultipleTimes() {
        roster.add(ri1);
        roster.add(ri2);
        roster.add(ri1);
        roster.remove(0);
        roster.remove(1);

        assertFalse(roster.isEmpty());
        assertEquals(1, roster.size());
        assertEquals(ri2, roster.getItem(0));
    }

    @Test
    public void clearTest() {
        roster.add(ri1);
        roster.add(ri2);
        roster.clear();

        assertTrue(roster.isEmpty());
        assertEquals(0, roster.size());
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
}