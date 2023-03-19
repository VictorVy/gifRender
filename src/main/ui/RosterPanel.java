package ui;

import javax.swing.*;
import java.awt.*;

// represents the panel which displays the roster
public class RosterPanel extends JPanel {
    public RosterPanel() {
        super();

        setBackground(GifRenderApp.ROSTER_BG_COLOUR);
        setLayout(new FlowLayout(FlowLayout.LEADING, 20, 20));
    }
}
