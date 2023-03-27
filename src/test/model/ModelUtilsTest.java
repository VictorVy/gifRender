package model;

import com.icafe4j.image.gif.GIFFrame;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Unit tests for the ModelUtils class
public class ModelUtilsTest {
    GIFFrame frame1 = new GIFFrame(new BufferedImage(100, 100, 1));
    GIFFrame frame2 = new GIFFrame(new BufferedImage(1920, 1080, 1));
    int d1 = 100;
    int d2 = 2550;
    GIFFrame frame1d = new GIFFrame(new BufferedImage(100, 100, 1), d1);
    GIFFrame frame2d = new GIFFrame(new BufferedImage(1920, 1080, 1), d2);
    ArrayList<RosterItem> frames = new ArrayList<>(Arrays.asList(new RosterItem(frame1, "1.png"),
            new RosterItem(frame2, "2.jpg"),
            new RosterItem(frame1d, "1d.png"),
            new RosterItem(frame2d, "2d.jpg")));

    @Test
    public void copyExceptDelayTest() {
        assertTrue(frameEquals(frame1d, ModelUtils.copyExceptDelay(frame1, d1)));
        assertTrue(frameEquals(frame2d, ModelUtils.copyExceptDelay(frame2, d2)));
    }

    @Test
    public void toGifFramesTest() {
        assertArrayEquals(new GIFFrame[]{frame1, frame2, frame1d, frame2d}, ModelUtils.toGifFrames(frames));
    }

    public static boolean frameEquals(GIFFrame expected, GIFFrame actual) {
        return imgEquals(expected.getFrame(), actual.getFrame()) &&
                expected.getLeftPosition() == actual.getLeftPosition() &&
                expected.getTopPosition() == actual.getTopPosition() &&
                expected.getDelay() == actual.getDelay() &&
                expected.getDisposalMethod() == actual.getDisposalMethod() &&
                expected.getUserInputFlag() == actual.getUserInputFlag() &&
                expected.getTransparencyFlag() == actual.getTransparencyFlag() &&
                expected.getTransparentColor() == actual.getTransparentColor();
    }

    private static boolean imgEquals(BufferedImage expected, BufferedImage actual) {
        //https://stackoverflow.com/questions/11006394/is-there-a-simple-way-to-compare-bufferedimage-instances

        if (expected.getWidth() != actual.getWidth() || expected.getHeight() != expected.getHeight()) {
            return false;
        }

        int w = expected.getWidth();
        int h = expected.getHeight();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (expected.getRGB(x, y) != actual.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }
}
