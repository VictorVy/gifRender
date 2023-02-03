package ui;

import com.icafe4j.image.gif.GIFTweaker;
import model.Roster;
import model.RosterItem;

//import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import com.icafe4j.image.ImageIO;

public class Main {
    private static Roster roster;
    private static Path outputDir;
    private static BufferedReader br;

    private static final String MANUAL = "man";
    private static final String EXIT = "exit";
    private static final String CLEAR = "clear";
    private static final String VIEW_ROSTER = "vr";
    private static final String ADD = "add ";
    private static final String REMOVE = "rm ";
    private static final String OUTPUT = "out";

    public static void main(String[] args) {
        init();

        System.out.println("Welcome to gifRender.\n"
                + "To view the manual, type \"man\".\n"
                + "Happy rending!");

        while (true) {
            System.out.println("\nEnter command:");

            try {
                String input = br.readLine();

                if (input.equals(EXIT)) {
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

    private static void init() {
        roster = new Roster();
        outputDir = Path.of("nodir");

        br = new BufferedReader(new InputStreamReader(System.in));
    }

    private static void handleInput(String input) throws Exception {
        if (input.equals(MANUAL)) {
            printManual();
        } else if (input.equals(CLEAR)) {
            clearScreen();
        } else if (input.equals(VIEW_ROSTER)) {
            viewRoster();
        } else if (input.startsWith(ADD)) {
            addItem(input.substring(4));
        } else if (input.startsWith(REMOVE)) {
            try {
                removeItem(Integer.parseInt(input.substring(3)));
            } catch (NumberFormatException e) {
                System.out.println("Not an integer!");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Item doesn't exist!");
            }
        } else if (input.equals(OUTPUT)) {
            outputRoster();
        } else {
            System.out.println("Invalid command!");
        }
    }

    private static void printManual() {
        System.out.println("Available commands:\n\n"
                + MANUAL + "\n\tPrints this manual.\n\n"
                + CLEAR + "\n\tClears the screen.\n\n"
                + EXIT + "\n\tExits the program.\n\n"
                + VIEW_ROSTER + "\n\tView items in the image roster.\n\n"
                + ADD + "p\n\tAdd the image at path p to the roster.\n\t\tExample: add D:\\Pictures\\example.png\n\n"
                + REMOVE + "i\n\tRemove the item at index i from the roster.\n\t\tExample: rm 0\n\n"
                + OUTPUT + "\n\tOutput the roster as a gif.");
    }

    private static void clearScreen() {
        // https://stackoverflow.com/questions/2979383/how-to-clear-the-console
        System.out.println(System.lineSeparator().repeat(50));
    }

    private static void viewRoster() {
        if (rosterIsEmpty()) {
            return;
        }

        System.out.println("Roster:");
        for (int i = 0; i < roster.size(); i++) {
            RosterItem ri = roster.getItem(i);
            System.out.println("\nIndex " + i + "\n\t" + ri.getName());
        }
    }

    private static void addItem(String inputPath) {
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

    private static void removeItem(int index) {
        System.out.println(roster.getItem(index).getName() + " was removed from index " + index + ".");
        roster.remove(index);
    }

    private static void outputRoster() throws Exception {
        if (rosterIsEmpty()) {
            return;
        }

        setOutputDir();

        if (confirm("Output GIF to " + outputDir + "?")) {
            makeGif();
            System.out.println("GIF created in " + outputDir + "!");
        }
    }

    private static void makeGif() throws Exception {
        System.out.println(roster.size());

        FileOutputStream out = new FileOutputStream(outputDir + "/test.jpg.gif");

        GIFTweaker.writeAnimatedGIF(roster.getFrames(), out);
    }

    private static void setOutputDir() throws IOException {
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

    private static boolean confirm(String message) throws IOException {
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

    private static boolean rosterIsEmpty() {
        if (roster.isEmpty()) {
            System.out.println("Your roster is empty!");
        }

        return roster.isEmpty();
    }

    private static Boolean isImageOrGif(String name) {
        String n = name.toLowerCase();
        return n.endsWith("png") || n.endsWith("jpg") || n.endsWith("bmp") || n.endsWith("gif");
    }
}

