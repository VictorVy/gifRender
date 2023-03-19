package ui;

import model.RosterItem;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

// represents a panel that displays a single RosterItem
public class ItemPanel extends JPanel {
    private RosterItem item;
    private BufferedImage thumbnail;

    // EFFECTS: constructs a new ItemPanel with the given label and button
    public ItemPanel(RosterItem ri, int index) {
        item = ri;
        thumbnail = IOUtils.makeThumb(ri.getImage(), GifRenderApp.MAX_THUMB_WIDTH, GifRenderApp.MAX_THUMB_HEIGHT);

        setLayout(new BorderLayout(5, 5));

        JLabel indexLabel = new JLabel(index + ": " + item.getName(), SwingConstants.CENTER);
        indexLabel.setForeground(Color.WHITE);

        JLabel delayLabel = new JLabel("delay: " + item.getDelay() + " ms", SwingConstants.CENTER);
        delayLabel.setForeground(Color.WHITE);

        add(indexLabel, BorderLayout.NORTH);
        add(new JLabel(new ImageIcon(thumbnail)), BorderLayout.CENTER);
        add(delayLabel, BorderLayout.SOUTH);

        setFont(new Font("Courier", Font.PLAIN, 20));
        setToolTipText(item.getWidth() + " x " + item.getHeight());
        setBackground(GifRenderApp.ROSTER_BG_COLOUR);
    }
}
