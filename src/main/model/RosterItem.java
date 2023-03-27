package model;

import com.icafe4j.image.gif.GIFFrame;
import org.json.JSONObject;
import persistence.Writable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

// Represents a single frame of the output gif
public class RosterItem implements Writable {
    private BufferedImage image;
    private GIFFrame frame;
    private String name;
    private int width;
    private int height;
    private int delay;
    private int transparency;

    EventLog log;

    // EFFECTS: constructs a new RosterItem based on the given BufferedImage and name
    public RosterItem(BufferedImage image, String name) {
        this(new GIFFrame(image), name);
    }

    // EFFECTS: constructs a new RosterItem based on the given BufferedImage, name, and transparency flag
    public RosterItem(BufferedImage image, String name, int transparency) {
        this(new GIFFrame(image, 0, 0, 0, 2, 0, transparency, 255), name);
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
        transparency = frame.getTransparencyFlag();

        log = EventLog.getInstance();
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

    public void setName(String name) {
        this.name = name;
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

    // REQUIRES: delay >= 0
    // MODIFIES: this
    // EFFECTS: sets the delay to the new delay rounded to the nearest 10 ms, and changes frame to reflect the new delay
    public void setDelay(int delay) {
        if (delay < 0) {
            throw new NumberFormatException();
        }

        int d = (int) Math.round(delay / 10.0) * 10;
        this.delay = d;
        frame = ModelUtils.copyExceptDelay(frame, d);

        log.logEvent(new Event("Set delay of " + name + " to " + d));
    }

    public int getTransparency() {
        return transparency;
    }


    public int getTransparentColor() {
        return frame.getTransparentColor();
    }

    public int getDisposableMethod() {
        return frame.getDisposalMethod();
    }

    // EFFECTS: returns JSON representation of this item
    @Override
    public JSONObject toJson() throws IOException {
        JSONObject json = new JSONObject();

        // https://stackoverflow.com/questions/15414259/java-bufferedimage-to-byte-array-and-back
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        byte[] imageBytes = output.toByteArray();

        String imageString = Base64.getEncoder().encodeToString(imageBytes);

        json.put("name", name);
        json.put("image", imageString);
        json.put("delay", delay);
        json.put("transparency", transparency);

        return json;
    }
}
