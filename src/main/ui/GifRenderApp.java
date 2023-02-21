package ui;

import com.icafe4j.image.ImageIO;
import com.icafe4j.image.gif.GIFFrame;
import model.Roster;
import model.RosterItem;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

// gifRender application
public class GifRenderApp {
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

    // EFFECTS: runs gifRender
    public GifRenderApp() {
        run();
    }

    // MODIFIES: this
    // EFFECTS: main user interface loop
    public void run() {
        init();

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

    // REQUIRES: inputPath is a valid path to an image or gif file
    // MODIFIES: this
    // EFFECTS: adds the file at inputPath to the roster
    private void addItem(String inputPath) {
        Path path = Paths.get(inputPath);
        File file = path.toFile();

        try {
            if (!file.exists() || !IOUtils.isImageOrGif(file.getName())) {
                throw new InvalidPathException(inputPath, "Invalid input path.");
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
    }

    // MODIFIES: this
    // EFFECTS: delegates the remove command to appropriate method
    private void handleRemove(String input) throws IOException {
        if (input.equals("all")) {
            removeAll();
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
    private void removeAll() throws IOException {
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
    private void outputRoster() throws Exception {
        if (rosterIsEmpty()) {
            return;
        }

        String outputDir = askOutputDir();
        String outputName = askName("Please specify output file name:");

        if (confirm("Output " + outputName + ".gif to " + outputDir + "?")) {
            IOUtils.writeGif(roster.getFrames(), outputDir, outputName);
            System.out.println(outputName + ".gif created in " + outputDir);
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
    }
}