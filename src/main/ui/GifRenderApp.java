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

    public GifRenderApp() {
        run();
    }

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

    private void init() {
        br = new BufferedReader(new InputStreamReader(System.in));
        roster = new Roster();
    }

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

    private void printManual() {
        System.out.println("Available commands:\n\n"
                + manComm + "\n\tPrint this manual.\n\n"
                + clearComm + "\n\tClear the console.\n\n"
                + exitComm + "\n\tExit the program.\n\n"
                + viewRosterComm + "\n\tView item in the image roster.\n\n"
                + addComm + "p\n\tAdd the image or gif at path p to the roster."
                + "\n\t\tadd D:\\Pictures\\example.png\n\n"
                + removeComm + "i\n\tRemove the item at index i from the roster."
                + "\n\t\trm 0\n\t\trm all\n\n"
                + downloadComm + "i\n\tDownload the item at index i as a png.\n\n"
                + delayComm + "i\n\tSet the delay of the roster item at index i."
                + "\n\t\td 0\n\t\td all\n\n"
                + outputComm + "\n\tOutput the roster as a gif.");
    }

    private void clearScreen() {
        // https://stackoverflow.com/questions/2979383/how-to-clear-the-console
        System.out.println(System.lineSeparator().repeat(50));
    }

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

    private void addItem(String inputPath) {
        Path path = Path.of(inputPath);
        File file = path.toFile();

        try {
            if (!file.exists() || !IOUtils.isImageOrGif(file.getName())) {
                throw new InvalidPathException(inputPath, "Invalid input path!");
            } else if (file.getName().toLowerCase().endsWith("gif")) {
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

    private void addImage(File file) throws Exception {
        roster.add(new RosterItem(ImageIO.read(file), file.getName()));
    }

    private void addGif(File file) throws Exception {
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));
        List<GIFFrame> frames = IOUtils.parseGif(file);

        for (int i = 0; i < frames.size(); i++) {
            roster.add(new RosterItem(frames.get(i), name + "_" + i + ".png"));
        }
    }

    private void handleRemove(String input) throws IOException {
        if (input.equals("all")) {
            removeAll();
        } else {
            removeItem(Integer.parseInt(input));
        }
    }

    private void removeItem(int index) throws IOException {
        if (confirm("Remove " + roster.getItem(index).getName() + " from the roster?")) {
            System.out.println(roster.getItem(index).getName() + " was removed from index " + index + ".");
            roster.remove(index);
        }
    }

    private void removeAll() throws IOException {
        if (confirm("Remove all items from the roster?")) {
            roster.clear();
            System.out.println("All items were removed from the roster.");
        }
    }

    private void handleDownload(String input) throws Exception {
        if (input.equals("all")) {
            downloadAll();
        } else {
            downloadItem(Integer.parseInt(input));
        }
    }

    private void downloadItem(int index) throws Exception {
        String itemName = roster.getItem(index).getName();
        String outputDir = askOutputDir();


        if (confirm("Download " + itemName + " to " + outputDir + "?")) {
            BufferedImage image = roster.getItem(index).getImage();

            IOUtils.writeImage(image, outputDir, itemName);
            System.out.println(itemName + " created in " + outputDir);
        }
    }

    private void downloadAll() throws Exception {
        String outputDir = askOutputDir();

        if (confirm("Download all roster items to " + outputDir + "?")) {
            for (int i = 0; i < roster.size(); i++) {
                RosterItem ri = roster.getItem(i);
                IOUtils.writeImage(ri.getImage(), outputDir, ri.getName());
            }

            System.out.println("Downloaded roster items to " + outputDir);
        }
    }

    private void handleDelay(String input) throws IOException {
        if (input.equals("all")) {
            setAllDelays();
        } else {
            setDelay(Integer.parseInt(input));
        }
    }

    private void setDelay(int index) throws IOException {
        RosterItem ri = roster.getItem(index);
        int delay = askDelay();

        ri.setDelay(delay);
        System.out.println("Delay set to " + delay + " ms for " + ri);
    }

    private void setAllDelays() throws IOException {
        int delay = askDelay();

        for (RosterItem ri : roster.getItems()) {
            ri.setDelay(delay);
        }

        System.out.println("Delay set to " + delay + " ms for all roster items.");
    }

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

    private boolean rosterIsEmpty() {
        if (roster.isEmpty()) {
            System.out.println("Your roster is empty!");
        }

        return roster.isEmpty();
    }
}