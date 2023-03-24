package ui;

import com.icafe4j.image.ImageIO;
import com.icafe4j.image.ImageType;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;
import com.icafe4j.image.reader.GIFReader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

// utility class for file input/output
public class IOUtils {
    // EFFECTS: inaccessible constructor
    private IOUtils() {

    }

    // EFFECTS: returns true if name indicates an image or gif file
    public static boolean isImageOrGif(String name) {
        String n = name.toLowerCase();
        return n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg")
                || n.endsWith(".bmp") || n.endsWith(".gif");
    }

    // EFFECTS: returns true if n is allowed as a file name
    public static boolean isLegalName(String n) {
        return !(n.contains("\\") || n.contains("/") || n.contains(":") || n.contains("*") || n.contains("?")
                || n.contains("\"") || n.contains("<") || n.contains(">") || n.contains("|"));
    }

    // EFFECTS: writes image to outputDir, named outputName
    public static void writeImage(BufferedImage image, String outputDir, String outputName) throws Exception {
        FileOutputStream out = new FileOutputStream(outputDir + "/" + outputName);
        String type = outputName.substring(outputName.lastIndexOf(".")).toLowerCase();

        if (type.equals("jpg")) {
            ImageIO.write(image, out, ImageType.JPG);
        } else if (type.equals("bmp")) {
            ImageIO.write(image, out, ImageType.BMP);
        } else {
            ImageIO.write(image, out, ImageType.PNG);
        }

        out.close();
    }

    // EFFECTS: writes frames as a gif to outputDir, named outputName
    public static void writeGif(GIFFrame[] frames, String outputDir, String outputName) throws Exception {
        FileOutputStream out = new FileOutputStream(outputDir + "/" + outputName + ".gif");

        GIFTweaker.writeAnimatedGIF(frames, out);

        out.close();
    }

    // EFFECTS: parses gif file and returns list of the frames
    public static List<GIFFrame> parseGif(File file) throws Exception {
        FileInputStream inputStream = new FileInputStream(file);

        GIFReader reader = new GIFReader();
        reader.read(inputStream);
        inputStream.close();

        return reader.getGIFFrames();
    }

    // EFFECTS: returns a thumbnail of the given image, with given size
    public static BufferedImage makeThumb(BufferedImage image, int maxWidth, int maxHeight) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width > maxWidth || height > maxHeight) {
            if (width > height) {
                height *= (double) maxWidth / width;
                width = maxWidth;
            } else {
                width *= (double) maxHeight / height;
                height = maxHeight;
            }
        }

        BufferedImage thumb = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = thumb.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, width, height, 0, 0, image.getWidth(),
                image.getHeight(), null);
        g.dispose();

        return thumb;
    }
}
