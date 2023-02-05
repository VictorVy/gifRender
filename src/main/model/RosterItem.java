package model;

import java.awt.image.BufferedImage;

// Represents a single frame of the output gif
public class RosterItem {
    private final BufferedImage image;
    private String name;

    public RosterItem(BufferedImage image, String name) {
        this.image = image;
        this.name = name;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
