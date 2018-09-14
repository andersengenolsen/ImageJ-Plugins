package plugins;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * Drawing a cumulative histogram, with grayscale under the histogram
 *
 * @author Anders Engen Olsen
 */
public class Cumulative_Histogram_Plugin implements PlugInFilter {
    private final String TITLE = "Cumulative histogram";
    private final int WIDTH = 256;
    private final int HEIGHT = 140;
    /**
     * Offset for grayscale under the histogram.
     */
    private final int OFFSET = 20;
    /**
     * Height of the histogram
     */
    private final int HISTO_HEIGHT = HEIGHT - OFFSET;

    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G;
    }

    /**
     * Drawing the histogram, with grayscale.
     */
    @Override
    public void run(ImageProcessor imageProcessor) {
        // Histogram init
        int[] hist = imageProcessor.getHistogram();
        int[] cumulHist = new int[WIDTH];

        // Defining scale for the cumulative histogram.
        // Max-value is W x H, dividing this with histogram height.
        int scaleFactor = (imageProcessor.getWidth() * imageProcessor.getHeight()) / (HISTO_HEIGHT);

        // ImageProcessor, fill with white (255)
        ImageProcessor cumulHistIp = new ByteProcessor(WIDTH, HEIGHT);
        cumulHistIp.setValue(255);
        cumulHistIp.fill();

        // Avoiding if-condition in for loop
        cumulHist[0] = hist[0];

        for (int i = 1; i < hist.length; i++) {

            // Calculating value in histogram
            cumulHist[i] = cumulHist[i - 1] + hist[i];

            // Drawing value in histogram
            cumulHistIp.setValue(0);
            cumulHistIp.drawLine(i, HISTO_HEIGHT, i, HISTO_HEIGHT - (cumulHist[i] / scaleFactor));

            // Drawing grayscale under image
            cumulHistIp.setValue(i);
            cumulHistIp.drawLine(i, HEIGHT, i, HISTO_HEIGHT);
        }

        // Displaying the cumulative histogram
        ImagePlus imagePlus = new ImagePlus(TITLE, cumulHistIp);
        imagePlus.show();
    }
}