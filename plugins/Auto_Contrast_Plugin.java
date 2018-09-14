package plugins;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * Plugin giving the user an option to set percentage when auto-contrasting.
 *
 * @author Anders Engen Olsen
 */
public class Auto_Contrast_Plugin implements PlugInFilter {

    private final static String TITLE = "Enter auto-contrast %";

    private GenericDialog dialog;

    private ImagePlus imagePlus;

    /**
     * Setting up dialog.
     */
    @Override
    public int setup(String s, ImagePlus imagePlus) {
        dialog = new GenericDialog(TITLE);
        dialog.addNumericField("Percentage: ", 0, 0);

        this.imagePlus = imagePlus;
        return DOES_8G;
    }

    /**
     * Performing auto-contrast on image.
     *
     * @see #contrastImage(ImageProcessor, int, int)
     */
    @Override
    public void run(ImageProcessor imageProcessor) {
        dialog.showDialog();

        if (dialog.wasCanceled())
            return;

        // Dimensions
        int width = imageProcessor.getWidth();
        int height = imageProcessor.getHeight();

        // Upper and lower quantiles. qLow = qHigh = q
        double q = getInput() / 100;

        int MN = width * height;

        // Upper and lower limit in the cumulative histogram
        int lowest = (int) (MN * q);
        int highest = (int) (MN * (1 - q));

        // Updated in next for loop
        // aLow = lower bound for lower quantile, aHigh = upper bound for upper quantile
        int aLow = 0;
        int aHigh = 0;

        // Histogram and cumulative histogram
        int[] hist = imageProcessor.getHistogram();
        int[] cumulHist = new int[256];

        cumulHist[0] = hist[0];

        for (int i = 1; i < hist.length; i++) {

            cumulHist[i] = cumulHist[i - 1] + hist[i];

            if (cumulHist[i] <= lowest)
                aLow = i;
            if (aHigh == 0 && cumulHist[i] >= highest)
                aHigh = i;
        }

        imagePlus.setProcessor(contrastImage(imageProcessor, aHigh, aLow));

        imagePlus.show();
    }

    /**
     * Performing auto-contrast on image.
     *
     * @param imageProcessor The imaage
     * @param aHigh          Upper bound. All px >= aHigh are set to aMax
     * @param aLow           Lower bound. All px =< aLow are set to aMin
     * @return new ImageProcessor, where all pixelvalues are changed. Values outside aHigh, aLow are
     * "smoothed" out.
     */
    private ImageProcessor contrastImage(ImageProcessor imageProcessor, int aHigh, int aLow) {
        // Min and max pixelvalues in image.
        int aMax = (int) imageProcessor.getMax();
        int aMin = (int) imageProcessor.getMin();

        int diff = aHigh - aLow;

        ImageProcessor ip = new ByteProcessor(imageProcessor.getWidth(), imageProcessor.getHeight());

        for (int x = 0; x < imageProcessor.getWidth(); x++) {
            for (int y = 0; y < imageProcessor.getHeight(); y++) {

                int current = imageProcessor.getPixel(x, y);

                // Changing values
                if (current >= aHigh)
                    current = aMax;
                else if (current <= aLow)
                    current = aMin;
                else {
                    current = (current - aLow) * 255 / diff;
                }

                ip.putPixel(x, y, current);
            }
        }
        return ip;
    }

    /**
     * @return input from dialog
     * @throws IllegalArgumentException Wrong input
     */
    private double getInput() {
        double in = dialog.getNextNumber();
        if (in <= 0 || in > 100)
            throw new IllegalArgumentException("Percentage must be between 0.1-100");

        return in;
    }
}
