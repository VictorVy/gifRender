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
    private Path outputDir;
    private String outputName;

    private final String noDir = "_nodir_";
    private final String noName = "_noname_";

    private final String manComm = "man";
    private final String exitComm = "exit";
    private final String clearComm = "clear";
    private final String viewRosterComm = "vr";
    private final String addComm = "add ";
    private final String removeComm = "rm ";
    private final String downloadComm = "down ";
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
        outputDir = Path.of(noDir);
        outputName = noName;
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
            removeItem(Integer.parseInt(input.substring(removeComm.length())));
        } else if (input.startsWith(downloadComm)) {
            downloadItem(Integer.parseInt(input.substring(downloadComm.length())));
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
                + downloadComm + "i\n\tDownload the item at index i as a png.\n\n"
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

    private void removeItem(int index) throws IOException {
        if (confirm("Remove " + roster.getItem(index).getName() + " from the roster?")) {
            System.out.println(roster.getItem(index).getName() + " was removed from index " + index + ".");
            roster.remove(index);
        }
    }

    private void downloadItem(int index) throws Exception {
        BufferedImage image = roster.getItem(index).getImage();

        setOutputIfNone();
        confirmOutput("Download " + outputName + ".png to " + outputDir + "?");

        IOUtils.writeImage(image, outputDir, outputName);
        System.out.println(outputName + ".png created in " + outputDir);
    }

    private void outputRoster() throws Exception {
        if (rosterIsEmpty()) {
            return;
        }

        setOutputIfNone();
        confirmOutput("Output " + outputName + ".gif to " + outputDir + "?");

        IOUtils.writeGif(roster.getFrames(), outputDir, outputName);
        System.out.println(outputName + ".gif created in " + outputDir);
    }

    private void setOutputIfNone() throws IOException {
        if (outputDir.toString().equals(noDir)) {
            setOutputDir();
        }
        if (outputName.equals(noName)) {
            setOutputName();
        }
    }


    private void confirmOutput(String msg) throws IOException {
        while (!confirm(msg)) {
            setOutputDir();
            setOutputName();
        }
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

    private void setOutputName() throws IOException {
        while (true) {
            System.out.println("Please specify output file name:");
            String input = br.readLine();

            if (IOUtils.isLegalName(input)) {
                outputName = input;
                break;
            } else {
                System.out.println("Invalid name!");
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