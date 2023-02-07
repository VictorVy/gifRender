package model;

import com.icafe4j.image.gif.GIFFrame;

import java.awt.image.BufferedImage;

// Represents a single frame of the output gif
public class RosterItem {
    private BufferedImage image;
    private GIFFrame frame;
    private String name;
    private int width;
    private int height;
    private int delay;

    // EFFECTS: constructs a new RosterItem based on the given BufferedImage and name
    public RosterItem(BufferedImage image, String name) {
        this(new GIFFrame(image), name);
    }

    // EFFECTS: constructs a new RosterItem based on the given GIFFrame and name
    //          width is set to the image's width, height is the image's height
    //          the delay is 0 by default, unless the given frame was from a gif
    public RosterItem(GIFFrame frame, String name) {
        image = frame.getFrame();
        this.name = name;
        this.frame = frame;

        width = image.getWidth();
        height = image.getHeight();
        delay = frame.getDelay() * 10;
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

    // MODIFIES: this
    // EFFECTS: sets the delay to the new delay, changes the GIFFrame to have the new delay
    public void setDelay(int delay) {
        this.delay = delay;
        frame = copyExceptDelay(delay);
    }

    // EFFECTS: returns a new GIFFrame with all the same properties as frame except for the delay
    private GIFFrame copyExceptDelay(int delay) {
        return new GIFFrame(image, frame.getLeftPosition(), frame.getTopPosition(), delay, frame.getDisposalMethod(),
                frame.getUserInputFlag(), frame.getTransparencyFlag(), frame.getTransparentColor());
    }

    // EFFECTS: returns the name
    @Override
    public String toString() {
        return getName();
    }
}
