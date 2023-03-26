package ui;

import javax.swing.*;

// represents a drop down menu item
public class DropDownItem extends JMenuItem {
    // EFFECTS: constructs a new DropDownItem with the given text
    public DropDownItem(String text) {
        super(text);
    }

    // EFFECTS: returns a string representation of this DropDownItem
    @Override
    public String toString() {
        return getText();
    }
}
