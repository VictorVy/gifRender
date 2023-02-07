package ui;

import com.icafe4j.image.ImageIO;
import com.icafe4j.image.gif.GIFFrame;
import model.Roster;
import model.RosterItem;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;

// gifRender application
public class GifRenderApp {
    private BufferedReader br;
    private Roster roster;

    private final String manComm = "man";
    private final String exitComm = "exit";
    private final String clearComm = "clear";
    private final String viewRosterComm = "vr";
    private final String addComm = "add ";
    private final String removeComm = "rm ";
    private final String downloadComm = "down ";
    private final String delayComm = "d ";
    private final String outputComm = "out";

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

                if (input.equals(exitComm)) {
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
    }

    // MODIFIES: this
    // EFFECTS: delegates tasks to appropriate method based on input
    private void handleInput(String input) throws Exception {
        if (input.equals(manComm)) {
            printManual();
        } else if (input.equals(clearComm)) {
            clearScreen();
        } else if (input.equals(viewRosterComm)) {
            viewRoster();
        } else if (input.startsWith(addComm)) {
            addItem(input.substring(addComm.length()));
        } else if (input.startsWith(removeComm)) {
            handleRemove(input.substring(removeComm.length()));
        } else if (input.startsWith(downloadComm)) {
            handleDownload(input.substring(downloadComm.length()));
        } else if (input.startsWith(delayComm)) {
            handleDelay(input.substring(delayComm.length()));
        } else if (input.equals(outputComm)) {
            outputRoster();
        } else {
            System.out.println("Invalid command!");
        }
    }

    // EFFECTS: prints the list of commands to the console
    private void printManual() {
        System.out.println("Available commands:\n\n"
                + manComm + "\n\tPrint this manual.\n\n"
                + clearComm + "\n\tClear the console.\n\n"
                + exitComm + "\n\tExit the program.\n\n"
                + viewRosterComm + "\n\tView all items in the image roster.\n\n"
                + addComm + "p\n\tAdd the image or gif at path p to the roster."
                + "\n\t\tadd D:\\Pictures\\example.png\n\n"
                + removeComm + "i\n\tRemove the item at index i from the roster."
                + "\n\t\trm 0\n\t\trm all\n\n"
                + downloadComm + "i\n\tDownload the item at index i."
                + "\n\t\tdown 0\n\t\tdown all\n\n"
                + delayComm + "i\n\tSet the delay of the item at index i. Rounded to the nearest 10 ms."
                + "\n\t\td 0\n\t\td all\n\n"
                + outputComm + "\n\tOutput the roster as a gif.");
    }

    // EFFECTS: "clears" the console
    private void clearScreen() {
        // https://stackoverflow.com/questions/2979383/how-to-clear-the-console
        System.out.println(System.lineSeparator().repeat(50));
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
        Path path = Path.of(inputPath);
        File file = path.toFile();

        try {
            if (!file.exists() || !IOUtils.isImageOrGif(file.getName())) {
                throw new InvalidPathException(inputPath, "Invalid input path.");
            } else if (file.getName().toLowerCase().endsWith(".gif")) {
                addGif(file);
            } else {
                addImage(file);
            }
            System.out.println(file.getName() + " added to roster.");
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
    // EFFECTS: adds the image file to the roster as a RosterItem
    private void addImage(File file) throws Exception {
        roster.add(new RosterItem(ImageIO.read(file), file.getName()));
    }

    // REQUIRES: file is a gif file
    // MODIFIES: this
    // EFFECTS: adds the frames of the gif file to the roster as RosterItems
    private void addGif(File file) throws Exception {
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));
        List<GIFFrame> frames = IOUtils.parseGif(file);

        for (int i = 0; i < frames.size(); i++) {
            roster.add(new RosterItem(frames.get(i), name + "_" + i + ".png"));
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
        System.out.println("Delay set to " + delay + " ms for " + ri);
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
        String outputName = askOutputName();

        if (confirm("Output " + outputName + ".gif to " + outputDir + "?")) {
            IOUtils.writeGif(roster.getFrames(), outputDir, outputName);
            System.out.println(outputName + ".gif created in " + outputDir);
        }
    }

    // EFFECTS: prompts user to specify an output path, and returns it
    private String askOutputDir() throws IOException {
        while (true) {
            System.out.println("Please specify output directory:");
            Path input = Path.of(br.readLine());

            if (input.toFile().exists() && input.toFile().isDirectory()) {
                return input.toString();
            } else {
                System.out.println("Invalid output path!");
            }
        }
    }

    // EFFECTS: prompts user to specify an output file name, and returns it
    private String askOutputName() throws IOException {
        while (true) {
            System.out.println("Please specify output file name:");
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
                int delay = Integer.parseInt(br.readLine());

                if (delay >= 0) {
                    return delay;
                }

                throw new NumberFormatException();
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

    // EFFECTS: if the roster is empty, notify the user and return true; otherwise return false
    private boolean rosterIsEmpty() {
        if (roster.isEmpty()) {
            System.out.println("Your roster is empty!");
        }

        return roster.isEmpty();
    }
}