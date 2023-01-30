package model;

import java.awt.image.BufferedImage;

public class RosterItem {
    private final BufferedImage image;

    public RosterItem(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }
}
