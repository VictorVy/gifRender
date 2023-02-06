package ui;

import com.icafe4j.image.ImageIO;
import com.icafe4j.image.ImageType;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;
import com.icafe4j.image.reader.GIFReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;

public class IOUtils {
    private IOUtils() {

    }

    public static boolean isImageOrGif(String name) {
        String n = name.toLowerCase();
        return n.endsWith("png") || n.endsWith("jpg") || n.endsWith("bmp") || n.endsWith("gif");
    }

    public static boolean isLegalName(String n) {
        return !(n.contains("\\") || n.contains("/") || n.contains(":") || n.contains("*") || n.contains("?")
                || n.contains("\"") || n.contains("<") || n.contains(">") || n.contains("|"));
    }

    public static void writeImage(BufferedImage image, Path outputDir, String outputName) throws Exception {
        FileOutputStream out = new FileOutputStream(outputDir + "/" + outputName + ".png");

        ImageIO.write(image, out, ImageType.PNG);
    }

    public static void writeGif(GIFFrame[] frames, Path outputDir, String outputName) throws Exception {
        FileOutputStream out = new FileOutputStream(outputDir + "/" + outputName + ".gif");

        GIFTweaker.writeAnimatedGIF(frames, out);
    }

    public static List<GIFFrame> parseGif(File file) throws Exception {
        FileInputStream inputStream = new FileInputStream(file);

        GIFReader reader = new GIFReader();
        reader.read(inputStream);
        return reader.getGIFFrames();
    }
}
