package ui;

import model.Roster;
import model.RosterItem;

//import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import com.icafe4j.image.ImageIO;

public class Main {
    private static Roster roster;

    public static void main(String[] args) {
        roster = new Roster();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Welcome to gifRender.\n"
                + "To view the manual, type \"man\".\n"
                + "Happy rending!\n");

        while (true) {
            String input = "";

            try {
                input = br.readLine();
            } catch (Exception e) {
                System.out.println("Error reading input!");
            }

            if (input.equals("exit")) {
                break;
            }

            handleInput(input);
        }

        System.out.println("Adios!");
    }

    private static void handleInput(String input) {
        if (input.equals("man")) {
            printManual();
        } else if (input.equals("clear")) {
            clearScreen();
        } else if (input.equals("vr")) {
            viewRoster();
        } else if (input.startsWith("add ")) {
            addItem(input.substring(4));
        } else if (input.startsWith("rm ")) {
            try {
                removeItem(Integer.parseInt(input.substring(3)));
            } catch (NumberFormatException e) {
                System.out.println("Not an integer!");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Item doesn't exist!");
            }
        } else {
            System.out.println("Invalid command!");
        }
    }

    private static void printManual() {
        System.out.println("Available commands:\n\n"
                + "manual\n\tPrints this manual.\n\n"
                + "clear\n\tClears the screen.\n\n"
                + "exit\n\tExits the program.\n\n"
                + "vr\n\tView items in the image roster.\n\n"
                + "add p\n\tAdd the image at path p to the roster.\n\t\tExample: add D:\\Pictures\\example.png\n\n"
                + "rm i\n\tRemove the item at index i from the roster.\n\t\tExample: rm 0");
    }

    private static void clearScreen() {
        // https://stackoverflow.com/questions/2979383/how-to-clear-the-console
        System.out.println(System.lineSeparator().repeat(50));
    }

    private static void viewRoster() {
        if (roster.isEmpty()) {
            System.out.println("Your roster is empty!");
        } else {
            System.out.println("Roster:");
            for (int i = 0; i < roster.size(); i++) {
                RosterItem ri = roster.getItem(i);
                System.out.println("\nIndex " + i + "\n\t" + ri.getName());
            }
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

    public static Boolean isImageOrGif(String name) {
        String n = name.toLowerCase();
        return n.endsWith("png") || n.endsWith("jpg") || n.endsWith("bmp") || n.endsWith("gif");
    }
}

