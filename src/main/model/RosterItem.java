package model;

import com.icafe4j.image.gif.GIFFrame;

import java.awt.image.BufferedImage;

// Represents a single frame of the output gif
public class RosterItem {
    private final BufferedImage image;
    private final GIFFrame frame;
    private String name;
    private int width;
    private int height;
    private int delay;

    public RosterItem(BufferedImage image, String name) {
        this(new GIFFrame(image), name);
    }

    public RosterItem(GIFFrame frame, String name) {
        image = frame.getFrame();
        this.name = name;
        this.frame = frame;

        width = image.getWidth();
        height = image.getHeight();
        delay = frame.getDelay();
    }

    public BufferedImage getImage() {
        return image;
    }

    public GIFFrame getFrame() {
        return frame;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDelay() {
        return delay;
    }
}
