package plugins;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * Reflecting image horizontally
 *
 * @author Anders Engen Olsen
 */
public class Reflection_Plugin implements PlugInFilter {

    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G;
    }

    /**
     * Reflecting horizontally.
     * - For every row:
     * - Switch pixel on right hand side with pixel on left hand side
     */
    @Override
    public void run(ImageProcessor imageProcessor) {
        int w = imageProcessor.getWidth();
        int h = imageProcessor.getHeight();
        int mid = w / 2;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x <= mid; x++) {

                // Current pixel values
                int lowPix = imageProcessor.getPixel(x, y);
                int highPix = imageProcessor.getPixel((w - x), y);

                // Switching
                imageProcessor.putPixel(x, y, highPix);
                imageProcessor.putPixel((w - x), y, lowPix);
            }
        }
    }
}
