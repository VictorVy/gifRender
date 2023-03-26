package ui;

import javax.swing.*;
import java.awt.*;

// represents the panel which displays the roster
public class RosterPanel extends JPanel {
    // EFFECTS: constructs a new RosterPanel
    public RosterPanel() {
        super();

        setBackground(GifRenderApp.PANEL_COLOUR);
        setLayout(new FlowLayout(FlowLayout.LEADING, 20, 20));
    }
}
