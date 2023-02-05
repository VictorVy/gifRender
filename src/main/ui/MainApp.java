package ui;

import com.icafe4j.image.gif.GIFTweaker;
import model.Roster;
import model.RosterItem;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import com.icafe4j.image.ImageIO;

public class MainApp {
    private Roster roster;
    private Path outputDir;
    private BufferedReader br;

    private final String manComm = "man";
    private final String exitComm = "exit";
    private final String clearComm = "clear";
    private final String viewRosterComm = "vr";
    private final String addComm = "add ";
    private final String removeComm = "rm ";
    private final String outputComm = "out";

    public MainApp() {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Adios!");
    }

    private void init() {
        roster = new Roster();
        outputDir = Path.of("nodir");

        br = new BufferedReader(new InputStreamReader(System.in));
    }

    private void handleInput(String input) throws Exception {
        if (input.equals(manComm)) {
            printManual();
        } else if (input.equals(clearComm)) {
            clearScreen();
        } else if (input.equals(viewRosterComm)) {
            viewRoster();
        } else if (input.startsWith(addComm)) {
            addItem(input.substring(4));
        } else if (input.startsWith(removeComm)) {
            try {
                removeItem(Integer.parseInt(input.substring(3)));
            } catch (NumberFormatException e) {
                System.out.println("Not an integer!");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Item doesn't exist!");
            }
        } else if (input.equals(outputComm)) {
            outputRoster();
        } else {
            System.out.println("Invalid command!");
        }
    }

    private void printManual() {
        System.out.println("Available commands:\n\n"
                + manComm + "\n\tPrints this manual.\n\n"
                + clearComm + "\n\tClears the screen.\n\n"
                + exitComm + "\n\tExits the program.\n\n"
                + viewRosterComm + "\n\tView items in the image roster.\n\n"
                + addComm + "p\n\tAdd the image at path p to the roster."
                          + "\n\t\tExample: add D:\\Pictures\\example.png\n\n"
                + removeComm + "i\n\tRemove the item at index i from the roster.\n\t\tExample: rm 0\n\n"
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
            System.out.println("\nIndex " + i + "\n\t" + ri.getName());
        }
    }

    private void addItem(String inputPath) {
        Path path = Path.of(inputPath);
        File file = path.toFile();

        try {
            if (!file.exists() || !isImageOrGif(file.getName())) {
                throw new InvalidPathException(inputPath, "Invalid input path!");
            }

            roster.add(new RosterItem(ImageIO.read(file), file.getName()));
            System.out.println(file.getName() + " added to index " + (roster.size() - 1) + ".");
        } catch (InvalidPathException e) {
            System.out.println("Invalid file path!");
        } catch (IOException e) {
            System.out.println("Error reading file!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeItem(int index) {
        System.out.println(roster.getItem(index).getName() + " was removed from index " + index + ".");
        roster.remove(index);
    }

    private void outputRoster() throws Exception {
        if (rosterIsEmpty()) {
            return;
        }

        if (outputDir.toString().equals("nodir")) {
            setOutputDir();
        }

        while (!confirm("Output GIF to " + outputDir + "?")) {
            setOutputDir();
        }

        makeGif();
        System.out.println("GIF created in " + outputDir + "!");
    }

    private void makeGif() throws Exception {
        System.out.println(roster.size());

        FileOutputStream out = new FileOutputStream(outputDir + "/test.jpg.gif");

        GIFTweaker.writeAnimatedGIF(roster.getFrames(), out);
    }

    private void setOutputDir() throws IOException {
        while (true) {
            System.out.println("Please specify output directory:");
            Path input = Path.of(br.readLine());

            if (input.toFile().exists() && input.toFile().isDirectory()) {
                outputDir = input;
                break;
            } else {
                System.out.println("Invalid output path!");
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

    private Boolean isImageOrGif(String name) {
        String n = name.toLowerCase();
        return n.endsWith("png") || n.endsWith("jpg") || n.endsWith("bmp") || n.endsWith("gif");
    }
}