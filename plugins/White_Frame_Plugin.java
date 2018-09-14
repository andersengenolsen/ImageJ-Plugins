package plugins;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.util.Arrays;

/**
 * Drawing white frame into the image
 *
 * @author Anders Engen Olsen
 */
public class White_Frame_Plugin implements PlugInFilter {

    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G;
    }

    /**
     * Drawing white frame into the image.
     * - Initialize two arrays with values 255 in every index.
     * - Put values into each side, both vertically and horizontally
     */
    @Override
    public void run(ImageProcessor imageProcessor) {
        int w = imageProcessor.getWidth();
        int h = imageProcessor.getHeight();

        int[] wWhite = new int[w];
        int[] hWhite = new int[h];

        // 255 for white
        Arrays.fill(wWhite, 255);
        Arrays.fill(hWhite, 255);

        // Drawing 10px on each side, horizontally and vertically.
        for (int i = 0; i < 10; i++) {
            imageProcessor.putRow(0, i, wWhite, w);
            imageProcessor.putRow(0, h - i, wWhite, w);
            imageProcessor.putColumn(i, 0, hWhite, h);
            imageProcessor.putColumn(w - i, 0, hWhite, h);
        }
    }
}
