package ui;

import com.icafe4j.image.ImageIO;
import com.icafe4j.image.gif.GIFFrame;
import model.Event;
import model.EventLog;
import model.Roster;
import model.RosterItem;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.InvalidPathException;
import java.util.HashSet;
import java.util.List;

// gifRender application
public class GifRenderApp extends JFrame {
    private Roster roster;

    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private static final String JSON_STORE = "./data/roster.json";

    public static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public static final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    public static final Color BG_COLOUR = new Color(50, 50, 55);
    public static final Color PANEL_COLOUR = new Color(100, 100, 105);
    public static final Color TEXT_COLOUR = new Color(210, 210, 215);

    public static final int MAX_THUMB_HEIGHT = 150;
    public static final int MAX_THUMB_WIDTH = 150;

    private JPanel rosterPanel;
    private JPanel buttonPanel;

    JMenu fileMenu;

    private JTextField inputField = new JTextField();

    // EFFECTS: runs gifRender
    public GifRenderApp() {
        super("gifRender");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                 | UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        init();
    }

    // MODIFIES: this
    // EFFECTS: sets up JFrame properties
    private void initFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                EventLog log = EventLog.getInstance();
                for (Event s : log) {
                    System.out.println(s);
                }
            }
        });

        setSize(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        setLocation(SCREEN_WIDTH / 2 - getWidth() / 2, SCREEN_HEIGHT / 2 - getHeight() / 2);

        setLayout(new BorderLayout(20, 20));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        try {
            setIconImage(ImageIO.read(new File("./data/icon.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        getContentPane().setBackground(BG_COLOUR);

        addChildren();

        updateRosterPanel();
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: adds subcomponents to the frame
    private void addChildren() {
        rosterPanel = new RosterPanel();
        dragDropInit();

        JScrollPane rosterScroll = new JScrollPane(rosterPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rosterScroll.getHorizontalScrollBar().setUnitIncrement(64);

        buttonPanel = new JPanel();
        buttonPanelInit();


        add(rosterScroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.NORTH);
        setJMenuBar(initMenuBar());
    }

    // MODIFIES: this
    // EFFECTS: initializes drag and drop functionality
    private void dragDropInit() {
        // https://stackoverflow.com/questions/811248/how-can-i-use-drag-and-drop-in-swing-to-get-file-path
        rosterPanel.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }

                try {
                    addItems((List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
                } catch (UnsupportedFlavorException | IOException e) {
                    return false;
                }

                return true;
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: initializes button panel
    private void buttonPanelInit() {
        buttonPanel.setBackground(PANEL_COLOUR);
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addFiles());
        addButton.setToolTipText("Add images and GIFs to the roster");

        JButton outButton = new JButton("Output");
        outButton.addActionListener(e -> saveFile());
        outButton.setToolTipText("Output the roster as a gif");

        buttonPanel.add(addButton, BorderLayout.WEST);
        buttonPanel.add(centerButtonPanel(), BorderLayout.CENTER);
        buttonPanel.add(outButton, BorderLayout.EAST);
    }

    private JPanel centerButtonPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(PANEL_COLOUR);

        JButton executeButton = new JButton("Remove");
        executeButton.setPreferredSize(new Dimension(90, 25));
        executeButton.setToolTipText("Execute operation");
        executeButton.addActionListener(e -> executeCommand(executeButton.getText()));

        DropDownItem removeItem = new DropDownItem("Remove");
        DropDownItem swapItem = new DropDownItem("Swap");
        DropDownItem shiftItem = new DropDownItem("Shift");
        DropDownItem renameItem = new DropDownItem("Rename");
        DropDownItem downloadItem = new DropDownItem("Download");
        DropDownItem delayItem = new DropDownItem("Delay");
        JComboBox<DropDownItem> opBox = new JComboBox<>(new DropDownItem[]{removeItem, swapItem, shiftItem,
                renameItem, downloadItem, delayItem});
        opBox.addActionListener(e -> executeButton.setText(opBox.getSelectedItem().toString()));
        opBox.setPreferredSize(new Dimension(90, 25));
        opBox.setToolTipText("Select operation to perform");

        inputField.setPreferredSize(new Dimension(250, 25));
        inputField.setToolTipText("Enter index, range of indices (i.e. \"0-19\"), or \"all\"");

        centerPanel.add(opBox);
        centerPanel.add(inputField);
        centerPanel.add(executeButton);

        return centerPanel;
    }

    // MODIFIES: this
    // EFFECTS: executes the command selected by the user
    private void executeCommand(String command) {
        String input = inputField.getText().trim();

        try {
            if (command.equals("Remove")) {
                executeRemove(parseRange(input));
            } else if (command.equals("Swap")) {
                executeSwap(parseTwo(input));
            } else if (command.equals("Shift")) {
                executeShift(parseTwo(input));
            } else if (command.equals("Rename")) {
                executeRename(parseOne(input));
            } else if (command.equals("Download")) {
                executeDownload(parseRange(input));
            } else if (command.equals("Delay")) {
                executeDelay(parseRange(input));
            }
        } catch (UnsupportedOperationException e) {
            JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        updateRosterPanel();
    }

    // MODIFIES: this
    // EFFECTS: removes the items at the indices in the range
    private void executeRemove(String range) {
        String[] indices = range.split("-");

        int i1 = Math.min(Integer.parseInt(indices[0]), Integer.parseInt(indices[1]));
        int i2 = Math.max(Integer.parseInt(indices[0]), Integer.parseInt(indices[1]));

        for (int i = i1; i < i2; i++) {
            roster.remove(i1);
        }
    }

    // MODIFIES: this
    // EFFECTS: swaps the items at the two indices
    private void executeSwap(String twoIndices) {
        String[] indices = twoIndices.split("-");

        int i1 = Integer.parseInt(indices[0]);
        int i2 = Integer.parseInt(indices[1]);

        swapItems(i1 + " " + i2);
    }

    // MODIFIES: this
    // EFFECTS: shifts the item at the first index to the second index
    private void executeShift(String twoIndices) {
        String[] indices = twoIndices.split("-");

        int i1 = Integer.parseInt(indices[0]);
        int i2 = Integer.parseInt(indices[1]);

        shiftItems(i1 + " " + i2);
    }

    // MODIFIES: this
    // EFFECTS: renames a roster item
    private void executeRename(int index) {
        RosterItem ri = roster.getItem(index);
        String oldName = ri.getName();

        String newName = JOptionPane.showInputDialog(this, "Enter new name for " + oldName + ":");

        if (!IOUtils.isLegalName(newName)) {
            JOptionPane.showMessageDialog(this, "Invalid name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        HashSet<String> names = new HashSet<>(roster.getNames());
        names.remove(oldName);

        if (!names.contains(newName.toLowerCase())) {
            ri.setName(newName);
            roster.rename(oldName, newName);

            JOptionPane.showMessageDialog(this, "Renamed " + oldName + " to " + newName + ".",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "An item named " + newName + " already exists",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // MODIFIES: this
    // EFFECTS: downloads the images at the indices in the range
    private void executeDownload(String range) {
        String[] indices = range.split("-");

        int i1 = Math.min(Integer.parseInt(indices[0]), Integer.parseInt(indices[1]));
        int i2 = Math.max(Integer.parseInt(indices[0]), Integer.parseInt(indices[1]));

        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                for (int i = i1; i < i2; i++) {
                    RosterItem ri = roster.getItem(i);
                    IOUtils.writeImage(ri.getImage(), fileChooser.getSelectedFile().getAbsolutePath(), ri.getName());
                }
            } catch (Exception e) {
                throw new UnsupportedOperationException();
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: sets the delay of the items at the indices in the range
    private void executeDelay(String range) {
        String[] indices = range.split("-");

        int i1 = Math.min(Integer.parseInt(indices[0]), Integer.parseInt(indices[1]));
        int i2 = Math.max(Integer.parseInt(indices[0]), Integer.parseInt(indices[1]));

        int delay;

        try {
            delay = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter delay (ms):"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid delay.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = i1; i < i2; i++) {
            roster.getItem(i).setDelay(delay);
        }
    }

    // EFFECTS: parses the input for the selection of one item
    private int parseOne(String input) throws UnsupportedOperationException {
        try {
            if (Integer.parseInt(input) < 0 || Integer.parseInt(input) >= roster.size()) {
                throw new UnsupportedOperationException();
            }

            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new UnsupportedOperationException();
        }
    }

    // EFFECTS: parses the input for the selection of two items
    private String parseTwo(String input) throws UnsupportedOperationException {
        try {
            String[] indices = input.split("-");

            int i1 = Integer.parseInt(indices[0].trim());
            int i2 = Integer.parseInt(indices[1].trim());

            if (indices.length != 2 || i1 < 0 || i2 < 0 || i1 >= roster.size() || i2 >= roster.size() || i1 == i2) {
                throw new UnsupportedOperationException();
            }

            return i1 + "-" + i2;
        } catch (NumberFormatException e) {
            throw new UnsupportedOperationException();
        }
    }

    // EFFECTS: parses the input for the selection of a range of items
    private String parseRange(String input) throws UnsupportedOperationException {
        try {
            if (input.equals("all")) {
                return "0-" + roster.size();
            } else if (input.contains("-")) {
                return parseTwo(input);
            } else {
                return parseOne(input) + "-" + (Integer.parseInt(input) + 1);
            }
        } catch (NumberFormatException e) {
            throw new UnsupportedOperationException();
        }
    }


    // MODIFIES: this
    // EFFECTS: initializes menu bar
    private JMenuBar initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        initFileMenu();
        fileMenu.setMnemonic(KeyEvent.VK_F);

        menuBar.add(fileMenu);

        return menuBar;
    }

    // MODIFIES: this
    // EFFECTS: initializes file menu
    private void initFileMenu() {
        DropDownItem addItem = new DropDownItem("Add");
        addItem.addActionListener(e -> addFiles());
        addItem.setMnemonic(KeyEvent.VK_A);

        DropDownItem saveItem = new DropDownItem("Save");
        saveItem.addActionListener(e -> saveRoster());
        saveItem.setMnemonic(KeyEvent.VK_S);

        DropDownItem loadItem = new DropDownItem("Load");
        loadItem.addActionListener(e -> loadRoster());
        loadItem.setMnemonic(KeyEvent.VK_L);

        DropDownItem outputItem = new DropDownItem("Output");
        outputItem.addActionListener(e -> saveFile());
        outputItem.setMnemonic(KeyEvent.VK_O);

        DropDownItem exitItem = new DropDownItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        exitItem.setMnemonic(KeyEvent.VK_E);

        fileMenu.add(addItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(outputItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitItem);
    }

    // MODIFIES: this
    // EFFECTS: prompts user to add files with a file chooser
    private void addFiles() {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setMultiSelectionEnabled(true);

        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || IOUtils.isImageOrGif(f.getName());
            }

            @Override
            public String getDescription() {
                return "Images and GIFs";
            }
        });

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            addItems(List.of(fileChooser.getSelectedFiles()));
        }
    }

    // EFFECTS: prompts user to save the roster as a gif
    private void saveFile() {
        if (roster.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Empty roster", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".gif");
            }

            @Override
            public String getDescription() {
                return "GIFs";
            }
        });


        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION
                && checkName(fileChooser.getSelectedFile())) {
            try {
                IOUtils.writeGif(roster.getFrames(), fileChooser.getSelectedFile().getAbsolutePath());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // EFFECTS: displays error dialog and returns false if file name is invalid, otherwise returns true
    private boolean checkName(File file) {
        if (!IOUtils.isLegalName(file.getName())) {
            JOptionPane.showMessageDialog(null, "Invalid name", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    // MODIFIES: this
    // EFFECTS: refreshes the displayed roster
    private void updateRosterPanel() {
        rosterPanel.removeAll();

        for (int i = 0; i < roster.size(); i++) {
            // inefficient, but backwards compatible with phase 2 model
            rosterPanel.add(new ItemPanel(roster.getItem(i), i));
        }

        if (roster.size() == 0) {
            JLabel emptyLabel = new JLabel("Add items or drag and drop");
            emptyLabel.setForeground(TEXT_COLOUR);
            rosterPanel.add(emptyLabel);
        }

        refreshRosterPanel();
    }

    // MODIFIES: this
    // EFFECTS: displays changes to rosterPanel
    private void refreshRosterPanel() {
        // https://stackoverflow.com/questions/1097366/java-swing-revalidate-vs-repaint
        rosterPanel.repaint();
        rosterPanel.revalidate();
    }

    // MODIFIES: this
    // EFFECTS: initializes buffered reader and roster
    private void init() {
        roster = new Roster();

        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);

        initFrame();
    }

    // REQUIRES: file contains only valid image or gif files
    // MODIFIES: this
    // EFFECTS: adds the files to the roster
    private void addItems(List<File> files) {
        for (File file : files) {
            addItem(file);
        }
    }

    // REQUIRES: file is a valid image or gif file
    // MODIFIES: this
    // EFFECTS: adds the file to the roster
    private void addItem(File file) {
        try {
            if (!file.exists() || !IOUtils.isImageOrGif(file.getName())) {
                throw new InvalidPathException(file.getPath(), "Invalid input path.");
            } else if (file.getName().toLowerCase().endsWith(".gif")) {
                addGif(file);
            } else {
                addImage(file);
            }
        } catch (InvalidPathException | FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Invalid path", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Invalid file", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // REQUIRES: file is an image file of type png, jpg, or bmp
    // MODIFIES: this
    // EFFECTS: adds the image file to the roster as a RosterItem, if no name collision
    private void addImage(File file) throws Exception {
        String n = file.getName();

        if (roster.containsName(n)) {
            JOptionPane.showMessageDialog(null, "An item named " + n + " already exists",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            roster.add(new RosterItem(ImageIO.read(file), n));
        }

        updateRosterPanel();
    }

    // REQUIRES: file is a gif file
    // MODIFIES: this
    // EFFECTS: adds the frames of the gif file to the roster as RosterItems, if no name collisions
    private void addGif(File file) throws Exception {
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));
        List<GIFFrame> frames = IOUtils.parseGif(file);

        for (int i = 0; i < frames.size(); i++) {
            String n = name + "_" + i + ".png";

            if (roster.containsName(n)) {
                JOptionPane.showMessageDialog(null, "An item named " + n + " already exists",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                roster.add(new RosterItem(frames.get(i), n));
            }
        }

        updateRosterPanel();
    }

    // MODIFIES: this
    // EFFECTS: swaps the item at index a with the item at index b
    private void swapItems(String input) {
        String[] split = input.split(" ");

        int a = Integer.parseInt(split[0]);
        int b = Integer.parseInt(split[1]);

        roster.swap(a, b);
    }

    // MODIFIES: this
    // EFFECTS: shifts the item at index a to index b
    private void shiftItems(String input) {
        String[] split = input.split(" ");

        int a = Integer.parseInt(split[0]);
        int b = Integer.parseInt(split[1]);

        roster.shift(a, b);
    }

    // adapted from CPSC 210 JsonSerializationDemo at https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

    // EFFECTS: saves the roster
    private void saveRoster() {
        try {
            jsonWriter.open();
            jsonWriter.write(roster);
            jsonWriter.close();
            JOptionPane.showMessageDialog(null, "Saved roster to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Unable to write to file: " + JSON_STORE,
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error", "Error", JOptionPane.ERROR_MESSAGE);
        }

        updateRosterPanel();
    }

    // MODIFIES: this
    // EFFECTS: loads saved roster
    private void loadRoster() {
        try {
            roster = jsonReader.read();
            JOptionPane.showMessageDialog(null, "Loaded roster from " + JSON_STORE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to read from file: " + JSON_STORE,
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error", "Error", JOptionPane.ERROR_MESSAGE);
        }

        updateRosterPanel();
    }
}