package model;

import com.icafe4j.image.gif.GIFFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class RosterItemTest {
    RosterItem ri1;
    RosterItem ri2;
    RosterItem ri3;
    String name1 = "test1.png";
    String name2 = "test2.jpg";
    BufferedImage img1 = new BufferedImage(100, 100, 1);
    BufferedImage img2 = new BufferedImage(1920, 1080, 1);
    GIFFrame frame1 = new GIFFrame(img1);
    GIFFrame frame2 = new GIFFrame(img2);
    GIFFrame frame3 = new GIFFrame(img1, 0, 0, 0, 2, 0, 1, 255);

    @BeforeEach
    public void runBefore() {
        ri1 = new RosterItem(img1, "test1.png");
        ri2 = new RosterItem(frame2, "test2.jpg");
        ri3 = new RosterItem(img1, "test1.png", 1);
    }

    @Test
    public void constructorTest() {
        assertEquals(img1, ri1.getImage());
        assertTrue(ModelUtilsTest.frameEquals(frame1, ri1.getFrame()));
        assertEquals(name1, ri1.getName());
        assertEquals(img1.getWidth(), ri1.getWidth());
        assertEquals(img1.getHeight(), ri1.getHeight());
        assertEquals(0, ri1.getDelay());
        assertEquals(0, ri1.getTransparency());

        assertEquals(img2, ri2.getImage());
        assertEquals(frame2, ri2.getFrame());
        assertEquals(name2, ri2.getName());
        assertEquals(img2.getWidth(), ri2.getWidth());
        assertEquals(img2.getHeight(), ri2.getHeight());
        assertEquals(0, ri2.getDelay());
        assertEquals(0, ri2.getTransparency());

        assertEquals(img1, ri3.getImage());
        assertTrue(ModelUtilsTest.frameEquals(frame3, ri3.getFrame()));
        assertEquals(name1, ri3.getName());
        assertEquals(img1.getWidth(), ri3.getWidth());
        assertEquals(img1.getHeight(), ri3.getHeight());
        assertEquals(0, ri3.getDelay());
        assertEquals(1, ri3.getTransparency());
        assertEquals(2, ri3.getDisposableMethod());
        assertEquals(255, ri3.getTransparentColor());
    }

    @Test
    public void setNameTest() {
        ri1.setName(".png");
        ri2.setName(" . .jpg");

        assertEquals(".png", ri1.getName());
        assertEquals(" . .jpg", ri2.getName());
    }

    @Test
    public void setDelayTest() {
        int d1 = 100;
        int d2 = 2550;

        ri1.setDelay(d1);
        ri2.setDelay(d2);

        assertEquals(d1, ri1.getDelay());
        assertEquals(d2, ri2.getDelay());
    }

    @Test
    public void setDelayRoundUp() {
        int d1 = 19;
        int d1r = 20;
        int d2 = 125;
        int d2r = 130;

        ri1.setDelay(d1);
        ri2.setDelay(d2);

        assertEquals(d1r, ri1.getDelay());
        assertEquals(d2r, ri2.getDelay());
    }

    @Test
    public void setDelayRoundDown() {
        int d1 = 1;
        int d1r = 0;
        int d2 = 194;
        int d2r = 190;

        ri1.setDelay(d1);
        ri2.setDelay(d2);

        assertEquals(d1r, ri1.getDelay());
        assertEquals(d2r, ri2.getDelay());
    }

    @Test
    public void setDelayNegative() {
        assertThrows(NumberFormatException.class, () -> ri1.setDelay(-1));
        assertThrows(NumberFormatException.class, () -> ri1.setDelay(-2504));

        assertEquals(0, ri1.getDelay());
        assertEquals(0, ri2.getDelay());
    }
}