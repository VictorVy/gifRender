package ui;

import com.icafe4j.image.ImageIO;
import com.icafe4j.image.gif.GIFFrame;
import model.Roster;
import model.RosterItem;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

// gifRender application
public class GifRenderApp extends JFrame {
    private BufferedReader br;
    private Roster roster;

    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private static final String JSON_STORE = "./data/roster.json";

    private static final String MAN_COMM = "man";
    private static final String EXIT_COMM = "exit";
    private static final String CLEAR_COMM = "clear";
    private static final String VIEW_ROSTER_COMM = "vr";
    private static final String ADD_COMM = "add ";
    private static final String REMOVE_COMM = "rm ";
    private static final String SWAP_COMM = "sw ";
    private static final String SHIFT_COMM = "sh ";
    private static final String RENAME_COMM = "ren ";
    private static final String DOWNLOAD_COMM = "down ";
    private static final String DELAY_COMM = "d ";
    private static final String OUTPUT_COMM = "out";
    private static final String SAVE_COMM = "sv";
    private static final String LOAD_COMM = "ld";

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

        init();
        run();
    }

    // MODIFIES: this
    // EFFECTS: sets up JFrame properties
    private void initFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
    // EFFECTS: executes the remove command
    private void executeRemove(String range) {
        String[] indices = range.split("-");

        int i1 = Math.min(Integer.parseInt(indices[0]), Integer.parseInt(indices[1]));
        int i2 = Math.max(Integer.parseInt(indices[0]), Integer.parseInt(indices[1]));

        for (int i = i1; i < i2; i++) {
            roster.remove(i);
        }
    }

    // MODIFIES: this
    // EFFECTS: executes the swap command
    private void executeSwap(String twoIndices) {
        String[] indices = twoIndices.split("-");

        int i1 = Integer.parseInt(indices[0]);
        int i2 = Integer.parseInt(indices[1]);

        swapItems(i1 + " " + i2);
    }

    // MODIFIES: this
    // EFFECTS: executes the shift command
    private void executeShift(String twoIndices) {
        String[] indices = twoIndices.split("-");

        int i1 = Integer.parseInt(indices[0]);
        int i2 = Integer.parseInt(indices[1]);

        shiftItems(i1 + " " + i2);
    }

    // MODIFIES: this
    // EFFECTS: executes the rename command
    private void executeRename(int index) {
        RosterItem ri = roster.getItem(index);
        String oldName = ri.getName();

        String newName = JOptionPane.showInputDialog(this, "Enter new name for " + oldName + ":");

        if (!IOUtils.isLegalName(newName)) {
            JOptionPane.showMessageDialog(this, "Invalid name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        rename(ri, oldName, newName + oldName.substring(oldName.lastIndexOf('.')));
    }

    // MODIFIES: this
    // EFFECTS: executes the download command
    private void executeDownload(String range) {
        String[] indices = range.split("-");

        int i1 = Math.min(Integer.parseInt(indices[0]), Integer.parseInt(indices[1]));
        int i2 = Math.max(Integer.parseInt(indices[0]), Integer.parseInt(indices[1]));

        JFileChooser fileChooser = new JFileChooser();

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
    // EFFECTS: executes the delay command
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
                return "0-" + (roster.size() - 1);
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
        JFileChooser fileChooser = new JFileChooser();
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
            File[] files = fileChooser.getSelectedFiles();

            for (File file : files) {
                addItem(file);
            }
        }
    }

    // EFFECTS: prompts user to save the roster as a gif
    private void saveFile() {
        if (roster.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Empty roster", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
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
    // EFFECTS: console user interface loop
    public void run() {
        System.out.println("Welcome to gifRender.\n"
                + "To view the manual, type \"man\".\n"
                + "Happy rending!");

        while (true) {
            System.out.println("\nEnter command:");

            try {
                String input = br.readLine();

                if (input.equals(EXIT_COMM)) {
                    break;
                }

                handleInput(input);

            } catch (IOException e) {
                System.out.println("Invalid input!");
                throw new RuntimeException(e);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Item doesn't exist!");
            } catch (Exception e) {
                System.out.println("Error.");
            }
        }

        System.out.println("Adios!");
    }

    // MODIFIES: this
    // EFFECTS: initializes buffered reader and roster
    private void init() {
        br = new BufferedReader(new InputStreamReader(System.in));
        roster = new Roster();

        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);

        initFrame();
    }

    // MODIFIES: this
    // EFFECTS: delegates tasks to appropriate method based on input
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void handleInput(String input) throws Exception {
        if (input.equals(MAN_COMM)) {
            printManual();
        } else if (input.equals(CLEAR_COMM)) {
            clearScreen();
        } else if (input.equals(VIEW_ROSTER_COMM)) {
            viewRoster();
        } else if (input.startsWith(ADD_COMM)) {
            addItem(input.substring(ADD_COMM.length()));
        } else if (input.startsWith(REMOVE_COMM)) {
            handleRemove(input.substring(REMOVE_COMM.length()));
        } else if (input.startsWith(SWAP_COMM)) {
            swapItems(input.substring(SWAP_COMM.length()));
        } else if (input.startsWith(SHIFT_COMM)) {
            shiftItems(input.substring(SHIFT_COMM.length()));
        } else if (input.startsWith(RENAME_COMM)) {
            renameItem(Integer.parseInt(input.substring(RENAME_COMM.length())));
        } else if (input.startsWith(DOWNLOAD_COMM)) {
            handleDownload(input.substring(DOWNLOAD_COMM.length()));
        } else if (input.startsWith(DELAY_COMM)) {
            handleDelay(input.substring(DELAY_COMM.length()));
        } else if (input.equals(OUTPUT_COMM)) {
            outputRoster();
        } else if (input.equals(SAVE_COMM)) {
            saveRoster();
        } else if (input.equals(LOAD_COMM)) {
            loadRoster();
        } else {
            System.out.println("Invalid command!");
        }
    }

    // EFFECTS: prints the list of commands to the console
    private void printManual() {
        System.out.println("Available commands:\n\n"
                + "> " + MAN_COMM + "\n\tPrint this manual.\n\n"
                + "> " + CLEAR_COMM + "\n\tClear the console.\n\n"
                + "> " + EXIT_COMM + "\n\tExit the program.\n\n"
                + "> " + VIEW_ROSTER_COMM + "\n\tView all items in the image roster.\n\n"
                + "> " + ADD_COMM + "p\n\tAdd the image or gif at path p to the roster.\n\t\t"
                + ADD_COMM + "D:\\Pictures\\example.png\n\n"
                + "> " + REMOVE_COMM + "i\n\tRemove the item at index i from the roster.\n\t\t"
                + REMOVE_COMM + "0\n\t\t" + REMOVE_COMM + "all\n\n"
                + "> " + SWAP_COMM + "a b\n\tSwap the positions of the items at index a and index b.\n\t\t"
                + SWAP_COMM + "42 19\n\n"
                + "> " + SHIFT_COMM + "a b\n\tShift the position of the item at index a to index b.\n\t\t"
                + SHIFT_COMM + "42 19\n\n"
                + "> " + RENAME_COMM + "i\n\tRename the item at index i.\n\t\t"
                + RENAME_COMM + "0\n\n"
                + "> " + DOWNLOAD_COMM + "i\n\tDownload the item at index i.\n\t\t"
                + DOWNLOAD_COMM + "0\n\t\t" + DOWNLOAD_COMM + "all\n\n"
                + "> " + DELAY_COMM + "i\n\tSet the delay of the item at index i. Rounded to the nearest 10 ms.\n\t\t"
                + DELAY_COMM + "0\n\t\t" + DELAY_COMM + "all\n\n"
                + "> " + OUTPUT_COMM + "\n\tOutput the roster as a gif.\n\n"
                + "> " + SAVE_COMM + "\n\tSave the roster.\n\n"
                + "> " + LOAD_COMM + "\n\tLoad the saved roster.");
    }

    // EFFECTS: "clears" the console
    private void clearScreen() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    // EFFECTS: displays all roster items and details
    private void viewRoster() {
        if (rosterIsEmpty()) {
            return;
        }

        System.out.println("Roster:");
        for (int i = 0; i < roster.size(); i++) {
            RosterItem ri = roster.getItem(i);
            System.out.println("\nIndex " + i
                    + "\n\tName: " + ri.getName()
                    + "\n\tDimensions: " + ri.getWidth() + "x" + ri.getHeight()
                    + "\n\tDelay: " + ri.getDelay());
        }
    }

    // REQUIRES: file contains only valid image or gif files
    // MODIFIES: this
    // EFFECTS: adds the files to the roster
    private void addItems(List<File> files) {
        for (File file : files) {
            addItem(file);
        }
    }

    // REQUIRES: inputPath is a valid path to an image or gif file
    // MODIFIES: this
    // EFFECTS: adds the file at inputPath to the roster
    private void addItem(String inputPath) {
        addItem(Paths.get(inputPath).toFile());
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
            System.out.println("Invalid file path!");
        } catch (IOException e) {
            System.out.println("Error reading file!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // REQUIRES: file is an image file of type png, jpg, or bmp
    // MODIFIES: this
    // EFFECTS: adds the image file to the roster as a RosterItem, if no name collision
    private void addImage(File file) throws Exception {
        String n = file.getName();

        if (roster.containsName(n)) {
            System.out.println("An item named " + n + " already exists!");
        } else {
            roster.add(new RosterItem(ImageIO.read(file), n));
            System.out.println(n + " added to roster.");
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
                System.out.println("An item named " + n + " already exists!");
            } else {
                roster.add(new RosterItem(frames.get(i), n));
                System.out.println(n + " added to roster.");
            }
        }

        updateRosterPanel();
    }

    // MODIFIES: this
    // EFFECTS: delegates the remove command to appropriate method
    private void handleRemove(String input) throws IOException {
        if (input.equals("all")) {
            removeAllItems();
        } else {
            removeItem(Integer.parseInt(input));
        }
    }

    // MODIFIES: this
    // EFFECTS: removes the item at index i from the roster
    private void removeItem(int i) throws IOException {
        if (confirm("Remove " + roster.getItem(i).getName() + " from the roster?")) {
            System.out.println(roster.getItem(i).getName() + " was removed from index " + i + ".");
            roster.remove(i);
        }
    }

    // MODIFIES: this
    // EFFECTS: removes all items from the roster
    private void removeAllItems() throws IOException {
        if (rosterIsEmpty()) {
            return;
        }

        if (confirm("Remove all items from the roster?")) {
            roster.clear();
            System.out.println("All items were removed from the roster.");
        }
    }

    // MODIFIES: this
    // EFFECTS: swaps the item at index a with the item at index b
    private void swapItems(String input) {
        String[] split = input.split(" ");

        int a = Integer.parseInt(split[0]);
        int b = Integer.parseInt(split[1]);

        roster.swap(a, b);

        System.out.println("Swapped " + roster.getItem(a).getName() + " and " + roster.getItem(b).getName());
    }

    // MODIFIES: this
    // EFFECTS: shifts the item at index a to index b
    private void shiftItems(String input) {
        String[] split = input.split(" ");

        int a = Integer.parseInt(split[0]);
        int b = Integer.parseInt(split[1]);

        roster.shift(a, b);

        System.out.println("Shifted " + roster.getItem(a).getName() + " to index " + b + ".");
    }

    // MODIFIES: this
    // EFFECTS: renames the roster item at index i, if no name collisions
    private void renameItem(int i) throws IOException {
        RosterItem ri = roster.getItem(i);
        String oldName = ri.getName();

        String newName = askName("Enter new name for " + oldName + ":")
                + oldName.substring(oldName.lastIndexOf("."));

        rename(ri, oldName, newName);
    }

    // MODIFIES: this
    // EFFECTS: renames the roster item at index i to newName, if no name collisions
    private void rename(RosterItem ri, String oldName, String newName) {
        HashSet<String> names = new HashSet<>(roster.getNames());
        names.remove(oldName);

        if (!names.contains(newName.toLowerCase())) {
            ri.setName(newName);
            roster.rename(oldName, newName);

            System.out.println(oldName + " has been renamed to " + newName);
        } else {
            System.out.println("An item named " + newName + " already exists!");
        }
    }

    // EFFECTS: delegates the download command to appropriate method
    private void handleDownload(String input) throws Exception {
        if (input.equals("all")) {
            downloadAll();
        } else {
            downloadItem(Integer.parseInt(input));
        }
    }

    // EFFECTS: downloads the roster item at index i to a specified path
    private void downloadItem(int i) throws Exception {
        String itemName = roster.getItem(i).getName();
        String outputDir = askOutputDir();


        if (confirm("Download " + itemName + " to " + outputDir + "?")) {
            BufferedImage image = roster.getItem(i).getImage();

            IOUtils.writeImage(image, outputDir, itemName);
            System.out.println(itemName + " created in " + outputDir);
        }
    }

    // EFFECTS: downloads all roster items to a specified path
    private void downloadAll() throws Exception {
        if (rosterIsEmpty()) {
            return;
        }

        String outputDir = askOutputDir();

        if (confirm("Download all roster items to " + outputDir + "?")) {
            for (int i = 0; i < roster.size(); i++) {
                RosterItem ri = roster.getItem(i);
                IOUtils.writeImage(ri.getImage(), outputDir, ri.getName());
            }

            System.out.println("Downloaded roster items to " + outputDir);
        }
    }

    // MODIFIES: this
    // EFFECTS: delegates the delay command to appropriate method
    private void handleDelay(String input) throws IOException {
        if (input.equals("all")) {
            setAllDelays();
        } else {
            setDelay(Integer.parseInt(input));
        }
    }

    // MODIFIES: this
    // EFFECTS: sets the delay for the roster item at index i
    private void setDelay(int i) throws IOException {
        RosterItem ri = roster.getItem(i);
        int delay = askDelay();

        ri.setDelay(delay);
        System.out.println("Delay set to " + delay + " ms for " + ri.getName());
    }

    // MODIFIES: this
    // EFFECTS: sets the delay for all roster items
    private void setAllDelays() throws IOException {
        if (rosterIsEmpty()) {
            return;
        }

        int delay = askDelay();

        for (RosterItem ri : roster.getItems()) {
            ri.setDelay(delay);
        }

        System.out.println("Delay set to " + delay + " ms for all roster items.");
    }

    // EFFECTS: outputs the roster to a specified path as a gif
    private void outputRoster() {
        if (rosterIsEmpty()) {
            return;
        }

        try {
            String outputDir = askOutputDir();
            String outputName = askName("Please specify output file name:");

            if (confirm("Output " + outputName + ".gif to " + outputDir + "?")) {
                IOUtils.writeGif(roster.getFrames(), outputDir, outputName);
                System.out.println(outputName + ".gif created in " + outputDir);
            }
        } catch (Exception e) {
            System.out.println("Error outputting roster");
        }
    }

    // EFFECTS: prompts user to specify an output path, and returns it
    private String askOutputDir() throws IOException {
        while (true) {
            System.out.println("Please specify output directory:");
            Path input = Paths.get(br.readLine());

            if (input.toFile().exists() && input.toFile().isDirectory()) {
                return input.toString();
            } else {
                System.out.println("Invalid output path!");
            }
        }
    }

    // EFFECTS: prompts user to specify an output file name, and returns it
    private String askName(String msg) throws IOException {
        while (true) {
            System.out.println(msg);
            String input = br.readLine();

            if (IOUtils.isLegalName(input)) {
                return input;
            } else {
                System.out.println("Invalid name!");
            }
        }
    }

    // EFFECTS: prompts user to specify a delay value (in milliseconds) and returns it
    private int askDelay() throws IOException {
        while (true) {
            System.out.println("Please specify delay (ms):");
            try {
                return Integer.parseInt(br.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Must be a non-negative integer!");
            }
        }
    }

    // EFFECTS: prompts user to enter Y or N in response to the message, returning true if Y and false if N
    private boolean confirm(String message) throws IOException {
        System.out.println(message);

        while (true) {
            System.out.println("Y/N");
            String answer = br.readLine().toLowerCase();

            if (!answer.equals("y") && !answer.equals("n")) {
                continue;
            }

            return answer.equals("y");
        }
    }

    // EFFECTS: if the roster is empty, notifies the user and returns true; otherwise returns false
    private boolean rosterIsEmpty() {
        if (roster.isEmpty()) {
            System.out.println("Your roster is empty!");
        }

        return roster.isEmpty();
    }

    // adapted from CPSC 210 JsonSerializationDemo at https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

    // EFFECTS: saves the roster
    private void saveRoster() {
        try {
            jsonWriter.open();
            jsonWriter.write(roster);
            jsonWriter.close();
            System.out.println("Saved roster to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        updateRosterPanel();
    }

    // MODIFIES: this
    // EFFECTS: loads saved roster
    private void loadRoster() {
        try {
            roster = jsonReader.read();
            System.out.println("Loaded roster from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("No saved roster.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateRosterPanel();
    }
}