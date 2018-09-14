package plugins;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/**
 * Plugin that shift the image horizontally.
 * Cycling shifting, image reappears at left side.
 *
 * @author Anders Engen Olsen
 */
public class Horizontal_Shifting_Plugin implements PlugInFilter {

    /**
     * Wait time before image is re-rendered.
     */
    private final static int WAIT_TIME = 25;

    /**
     * ImagePlus, metadata and {@link ImageProcessor} og {@link java.awt.Image}
     */
    private ImagePlus imagePlus;

    @Override
    public int setup(String s, ImagePlus imagePlus) {
        this.imagePlus = imagePlus;
        return DOES_8G;
    }

    /**
     * Calling two methods, which both have the same functionality.
     *
     * @see #shiftInOneLoop(ImageProcessor)
     * @see #shiftInTwoLoops(ImageProcessor)
     */
    @Override
    public void run(ImageProcessor imageProcessor) {
        shiftInOneLoop(imageProcessor);
        shiftInTwoLoops(imageProcessor);
    }

    /**
     * Shifting image horizontally, reappearing on left side.
     * Done with 1 for loop.
     * - Fetching vertical pixel at right side.
     * - Shifting the whole image 1 px to the right.
     * - Placing the vertical pixels on th left side.
     * <p>
     * Time complexity: O(N²)
     */
    private void shiftInOneLoop(ImageProcessor imageProcessor) {
        int w = imageProcessor.getWidth();
        int h = imageProcessor.getHeight();
        int[] col = new int[h];

        for (int x = 0; x < w; x++) {
            // Fetching right side pixels
            imageProcessor.getColumn(w - 1, 0, col, h);
            // Shifting image to the right
            imageProcessor.translate(1, 0);
            // Placing right side pixels on the left side.
            imageProcessor.putColumn(0, 0, col, h);
            // Delay and draw
            IJ.wait(WAIT_TIME);
            imagePlus.updateAndDraw();
        }
    }

    /**
     * Shifting image horizontally, reappearing on left side.
     * Done with 2 for loops.
     * <p>
     * - Fetching 2D array
     * - Shifting all arrays 1 px to the right on the x axis
     * - The last index will be reset to position 0, so the image will reappear.
     * - int[pos][pos] = int[pos++][pos] after 1 iteration
     */
    private void shiftInTwoLoops(ImageProcessor imageProcessor) {
        int w = imageProcessor.getWidth();
        int[][] coordinates = imageProcessor.getIntArray();

        // Looping through on the x axis
        for (int n = 0; n < w; n++) {

            int temp[] = coordinates[0];

            // Moving whole picture 1 px to the right.
            for (int i = 0; i < coordinates.length; i++) {
                // Resetting highest index to 0
                if (i == coordinates.length - 1) {
                    coordinates[0] = coordinates[coordinates.length - 1];
                    break;
                }

                int[] next = coordinates[i + 1];
                coordinates[i + 1] = temp;
                temp = next;
            } // 2D-array updated, moved 1 px up

            // Delay and draw.
            IJ.wait(WAIT_TIME);
            imageProcessor.setIntArray(coordinates);
            imagePlus.updateAndDraw();
        }
    }
}
