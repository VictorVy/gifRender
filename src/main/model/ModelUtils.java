package model;

import com.icafe4j.image.gif.GIFFrame;

import java.util.ArrayList;
import java.util.Arrays;

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
        Object[] arr = items.stream().map(RosterItem::getFrame).toArray();
        //https://stackoverflow.com/questions/12210311/downcasting-of-arrays-in-java
        return Arrays.copyOf(arr, arr.length, GIFFrame[].class);
    }
}
