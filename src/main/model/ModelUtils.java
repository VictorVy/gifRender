package model;

import com.icafe4j.image.gif.GIFFrame;

import java.util.ArrayList;

// Utility class for the model package
public class ModelUtils {
    // EFFECTS: inaccessible constructor
    private ModelUtils() {

    }

    // EFFECTS: returns a new GIFFrame with all the same properties as frame except for the delay
    public static GIFFrame copyExceptDelay(GIFFrame frame, int delay) {
        return new GIFFrame(frame.getFrame(), frame.getLeftPosition(), frame.getTopPosition(),
                delay, frame.getDisposalMethod(), frame.getUserInputFlag(),
                frame.getTransparencyFlag(), frame.getTransparentColor());
    }

    // EFFECTS: returns the list of RosterItems as an array of GIFFrames
    public static GIFFrame[] toGifFrames(ArrayList<RosterItem> items) {
        GIFFrame[] frames = new GIFFrame[items.size()];

        for (int i = 0; i < items.size(); i++) {
            frames[i] = items.get(i).getFrame();
        }

        return frames;
    }
}
