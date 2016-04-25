package edu.gatech.cs6475.seamcarving;

import java.awt.Color;

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private double[][] distTo;
    private int[][] verticeTo;
    private double[][] energies;
    private Picture picture;

    /**
     * Create a seam carver object based on the given picture
     */
    public SeamCarver(final Picture picture) {
        // create a seam carver object based on the given picture
        this.picture = picture;
    }

    private double xGradientSquare(final int x, final int y) {
        final Color first = picture.get(x + 1, y);
        final Color second = picture.get(x - 1, y);
        return squareColors(first, second);
    }

    private double yGradientSquare(final int x, final int y) {
        final Color first = picture.get(x, y + 1);
        final Color second = picture.get(x, y - 1);
        return squareColors(first, second);
    }

    private double squareColors(final Color first, final Color second) {
        final double sum = Math.pow(first.getRed() - second.getRed(), 2)
                        + Math.pow(first.getGreen() - second.getGreen(), 2)
                        + Math.pow(first.getBlue() - second.getBlue(), 2);
        return sum;
    }

    private double[][] energyMatrix() {
        final double[][] energyMatrix = new double[width()][height()];
        for (int col = 0; col < energyMatrix.length; col++) {
            for (int row = 0; row < energyMatrix[col].length; row++) {
                energyMatrix[col][row] = energy(col, row);
            }
        }
        return energyMatrix;
    }

    private boolean isValidCell(final int col, final int row) {
        return col >= 0 && row >= 0 && col < width() && row < height();
    }

    private void relax(final int relaxingCol, final int relaxingRow, final int originCol,
                    final int originRow) {
        if (isValidCell(relaxingCol, relaxingRow) && isValidCell(originCol, originRow)) {
            if (distTo[relaxingCol][relaxingRow] > distTo[originCol][originRow]
                            + energies[relaxingCol][relaxingRow]) {
                distTo[relaxingCol][relaxingRow] =
                                distTo[originCol][originRow] + energies[relaxingCol][relaxingRow];
                verticeTo[relaxingCol][relaxingRow] = originCol;
            }
        }
    }

    private Picture transpose(final Picture pic) {
        final Picture transpose = new Picture(pic.height(), pic.width());
        for (int col = 0; col < transpose.width(); col++) {
            for (int row = 0; row < transpose.height(); row++) {
                transpose.set(col, row, pic.get(row, col));
            }
        }
        return transpose;
    }

    /**
     * @return current picture
     */
    public Picture picture() {
        return picture;
    }

    /**
     * @return width of current picture
     */
    public int width() {
        return picture.width();
    }

    /**
     * @return height of current picture
     */
    public int height() {
        return picture.height();
    }

    /**
     * @return energy of pixel at column x and row y
     */
    public double energy(final int x, final int y) {
        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
            return 1000;
        }
        return Math.sqrt(xGradientSquare(x, y) + yGradientSquare(x, y));
    }

    /**
     * @return sequence of indices for horizontal seam
     */
    public int[] findHorizontalSeam() {
        picture = transpose(picture);
        final int[] seam = findVerticalSeam();
        picture = transpose(picture);
        return seam;
    }

    /**
     * @return sequence of indices for vertical seam
     */
    public int[] findVerticalSeam() {
        distTo = new double[width()][height()];
        verticeTo = new int[width()][height()];
        energies = energyMatrix();

        for (int col = 0; col < distTo.length; col++) {
            for (int row = 0; row < distTo[col].length; row++) {
                distTo[col][row] = Double.POSITIVE_INFINITY;
            }
        }

        for (int col = 0; col < distTo.length; col++) {
            distTo[col][0] = 0d;
        }

        for (int row = 0; row < height() - 1; row++) {
            for (int col = 0; col < width(); col++) {
                relax(col - 1, row + 1, col, row);
                relax(col, row + 1, col, row);
                relax(col + 1, row + 1, col, row);
            }
        }

        double minEnergy = Double.POSITIVE_INFINITY;
        int minCol = -1;
        for (int col = 0; col < width(); col++) {
            if (distTo[col][height() - 1] < minEnergy) {
                minEnergy = distTo[col][height() - 1];
                minCol = col;
            }
        }

        final int[] seam = new int[height()];
        seam[seam.length - 1] = minCol;
        for (int row = seam.length - 2; row >= 0; row--) {
            seam[row] = verticeTo[seam[row + 1]][row + 1];
        }
        return seam;
    }

    /**
     * Remove horizontal seam from current picture
     */
    public void removeHorizontalSeam(final int[] seam) {
        picture = transpose(picture);
        removeVerticalSeam(seam);
        picture = transpose(picture);
    }

    /**
     * Remove vertical seam from current picture
     */
    public void removeVerticalSeam(final int[] seam) {
        final Picture removed = new Picture(width() - 1, height());
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < seam[row]; col++) {
                removed.set(col, row, picture.get(col, row));
            }
            for (int col = seam[row] + 1; col < width(); col++) {
                removed.set(col - 1, row, picture.get(col, row));
            }
        }
        picture = removed;
    }
}
