package plugins;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * Reflecting image over its diagonal, 45 degrees to the right.
 *
 * @author Anders Engen Olsen
 */
public class Reflect_Diagonal_Plugin implements PlugInFilter {

    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G;
    }

    /**
     * Reflecting the image.
     * - Fetching dimensions
     * - Setting height = width, and width = height.
     * - Creating new ImageProcessor with new dimensions.
     * - For every row, starting on the right side:
     * - Fill every row with pixels that previously was in the columns.
     */
    @Override
    public void run(ImageProcessor imageProcessor) {
        // Dimensions
        int newWidth = imageProcessor.getHeight();
        int newHeight = imageProcessor.getWidth();
        int coordinates[][] = imageProcessor.getIntArray();

        // Creating new ImageProcessor
        imageProcessor = new ByteProcessor(newWidth, newHeight);

        // New image
        ImagePlus newImage = new ImagePlus("Speilvendt", imageProcessor);

        // Loop through all rows
        for (int y = 0; y < newHeight; y++) {
            // Fill every row with pixels that previously was in the columns.
            for (int x = 0; x < newWidth; x++) {
                imageProcessor.putPixel(newWidth - x - 1, y, coordinates[y][x]);
            }
        }

        newImage.show();
    }
}
