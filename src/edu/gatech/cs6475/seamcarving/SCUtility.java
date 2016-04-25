package edu.gatech.cs6475.seamcarving;

/******************************************************************************
 *  Compilation:  javac SCUtility.java
 *  Execution:    none
 *  Dependencies: SeamCarver.java
 *
 *  Some utility functions for testing SeamCarver.java.
 *
 ******************************************************************************/

import java.awt.Color;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdRandom;

public class SCUtility {
    // create random width-by-height array of tiles
    public static Picture randomPicture(final int width, final int height) {
        final Picture picture = new Picture(width, height);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                final int r = StdRandom.uniform(255);
                final int g = StdRandom.uniform(255);
                final int b = StdRandom.uniform(255);
                final Color color = new Color(r, g, b);
                picture.set(col, row, color);
            }
        }
        return picture;
    }

    public static double[][] toEnergyMatrix(final SeamCarver sc) {
        final double[][] returnDouble = new double[sc.width()][sc.height()];
        for (int col = 0; col < sc.width(); col++)
            for (int row = 0; row < sc.height(); row++)
                returnDouble[col][row] = sc.energy(col, row);

        return returnDouble;
    }

    // displays grayvalues as energy (converts to picture, calls show)
    public static void showEnergy(final SeamCarver sc) {
        doubleToPicture(toEnergyMatrix(sc)).show();
    }

    public static Picture toEnergyPicture(final SeamCarver sc) {
        final double[][] energyMatrix = toEnergyMatrix(sc);
        return doubleToPicture(energyMatrix);
    }

    // converts a double matrix of values into a normalized picture
    // values are normalized by the maximum grayscale value (ignoring border pixels)
    public static Picture doubleToPicture(final double[][] grayValues) {

        // each 1D array in the matrix represents a single column, so number
        // of 1D arrays is the width, and length of each array is the height
        final int width = grayValues.length;
        final int height = grayValues[0].length;

        final Picture picture = new Picture(width, height);

        // maximum grayscale value (ignoring border pixels)
        double maxVal = 0;
        for (int col = 1; col < width-1; col++) {
            for (int row = 1; row < height-1; row++) {
                if (grayValues[col][row] > maxVal)
                    maxVal = grayValues[col][row];
             }
        }

        if (maxVal == 0)
            return picture; //return black picture

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                float normalizedGrayValue = (float) grayValues[col][row] / (float) maxVal;
                if (normalizedGrayValue >= 1.0f) normalizedGrayValue = 1.0f;
                picture.set(col, row, new Color(normalizedGrayValue, normalizedGrayValue, normalizedGrayValue));
            }
        }

        return picture;
    }

    // This method is useful for debugging seams. It overlays red
    // pixels over the calculate seam. Due to the lack of a copy
    // constructor, it also alters the original picture.
    public static Picture seamOverlay(final Picture picture, final boolean horizontal, final int[] seamIndices) {
        final Picture overlaid = new Picture(picture.width(), picture.height());
        final int width = picture.width();
        final int height = picture.height();

        for (int col = 0; col < width; col++)
            for (int row = 0; row < height; row++)
                overlaid.set(col, row, picture.get(col, row));

        //if horizontal seam, then set one pixel in every column
        if (horizontal) {
            for (int col = 0; col < width; col++)
                overlaid.set(col, seamIndices[col], Color.RED);
        }
        else  { // if vertical, put one pixel in every row
            for (int row = 0; row < height; row++)
                overlaid.set(seamIndices[row], row, Color.RED);
        }

        return overlaid;
    }
}
