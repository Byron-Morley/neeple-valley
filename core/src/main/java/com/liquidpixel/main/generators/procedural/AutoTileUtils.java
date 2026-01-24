package com.liquidpixel.main.generators.procedural;

public class AutoTileUtils {

    /**
     * Value of location must be between 0 and 1
     *
     * @param layerTiles
     * @param x
     * @param y
     * @return
     */
    public static int calculateAutoTile(double[][] layerTiles, int x, int y) {

        int topLeft = getCorner(layerTiles, x - 1, y + 1) * 8;
        int topRight = getCorner(layerTiles, x + 1, y + 1);
        int bottomLeft = getCorner(layerTiles, x - 1, y - 1) * 4;
        int bottomRight = getCorner(layerTiles, x + 1, y - 1) * 2;

        return topLeft + topRight + bottomLeft + bottomRight;
    }

    public static int getCorner(double[][] layerTiles, int x, int y) {
        try {
            return (int) layerTiles[x][y];
        } catch (IndexOutOfBoundsException exception) {
            return 0;
        }
    }


    public static int calculateTileIndex(boolean top, boolean right, boolean bottom, boolean left){

        int topScore = top ? 1 : 0;
        int rightScore = right ? 2 : 0;
        int bottomScore = bottom ? 4 : 0;
        int leftScore = left ? 8 : 0;

        return topScore + rightScore + bottomScore + leftScore;
    }

    /**
     * balloon the array twice the size
     *
     * @return
     */
    public static int[][] balloonArray(double[][] multiArray) {


        int width = multiArray.length;
        int height = multiArray[0].length;

        int[][] balloonedArray = new int[width * 2][height * 2];

        for (int y = 0; y < multiArray.length; y++) {
            for (int x = 0; x < multiArray[y].length; x++) {

                int value = (int) Math.abs(multiArray[x][y]);

                int dx = x * 2;
                int dy = y * 2;

                balloonedArray[dx + 1][dy] = value;
                balloonedArray[dx + 1][dy + 1] = value;
                balloonedArray[dx][dy + 1] = value;
                balloonedArray[dx][dy] = value;
            }
        }
        return balloonedArray;
    }

    public static int[][] balloonArrayAndShift(double[][] multiArray, int modifier) {

        int width = multiArray.length;
        int height = multiArray[0].length;

        int[][] balloonedArray = new int[(width * 2) + modifier + 1][(height * 2) + modifier + 1];

        for (int y = 0; y < multiArray.length; y++) {
            for (int x = 0; x < multiArray[y].length; x++) {

                int value = (int) Math.abs(multiArray[x][y]);

                int dx = (x * 2) + modifier;
                int dy = (y * 2) + modifier;

                if (x == 0 && y == 0) {
                    balloonedArray[x * 2][y * 2] = value;
                }
                if (x == 0) {
                    balloonedArray[x * 2][dy] = value;
                    balloonedArray[x * 2][dy + 1] = value;
                }
                if (y == 0) {
                    balloonedArray[dx][y * 2] = value;
                    balloonedArray[dx + 1][y * 2] = value;
                }
                if (x == multiArray[y].length - 1) {
                    balloonedArray[x * 2 + 3][dy] = value;
                    balloonedArray[x * 2 + 3][dy + 1] = value;
                }
                if (y == 0 && x == multiArray[y].length - 1) {
                    balloonedArray[x * 2 + 3][dy - 1] = value;
                }

                balloonedArray[dx][dy] = value;
                balloonedArray[dx + 1][dy] = value;
                balloonedArray[dx + 1][dy + 1] = value;
                balloonedArray[dx][dy + 1] = value;
            }
        }
        return balloonedArray;
    }

    public static int[][] balloonArrayAndShift(double[][] multiArray, int modifier, int offsetX, int offsetY, int width, int height) {

        int[][] balloonedArray = new int[(width * 2) + modifier + 1][(height * 2) + modifier + 1];

        for (int y = offsetY; y < height; y++) {
            for (int x = offsetX; x < width; x++) {

                int value = (int) Math.abs(multiArray[x][y]);

                int dx = (x * 2) + modifier;
                int dy = (y * 2) + modifier;

                if (x == 0 && y == 0) {
                    balloonedArray[x * 2][y * 2] = value;
                }
                if (x == 0) {
                    balloonedArray[x * 2][dy] = value;
                    balloonedArray[x * 2][dy + 1] = value;
                }
                if (y == 0) {
                    balloonedArray[dx][y * 2] = value;
                    balloonedArray[dx + 1][y * 2] = value;
                }
                if (x == multiArray[y].length - 1) {
                    balloonedArray[x * 2 + 3][dy] = value;
                    balloonedArray[x * 2 + 3][dy + 1] = value;
                }
                if (y == 0 && x == multiArray[y].length - 1) {
                    balloonedArray[x * 2 + 3][dy - 1] = value;
                }

                balloonedArray[dx][dy] = value;
                balloonedArray[dx + 1][dy] = value;
                balloonedArray[dx + 1][dy + 1] = value;
                balloonedArray[dx][dy + 1] = value;
            }
        }
        return balloonedArray;
    }

    public static int[][] shrinkArray(int[][] balloonedArray) {

        int width = balloonedArray.length / 2;
        int height = balloonedArray[0].length / 2;

        int[][] multiArray = new int[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int dx = (x * 2);
                int dy = (y * 2);

                int bottomLeft = balloonedArray[dx][dy] * 4;
                int bottomRight = balloonedArray[dx + 1][dy] * 2;
                int topRight = balloonedArray[dx + 1][dy + 1];
                int topLeft = balloonedArray[dx][dy + 1] * 8;

                int score = bottomLeft + bottomRight + topRight + topLeft;

                multiArray[x][y] = score;
            }
        }
        return multiArray;
    }

    public static void printArray(double[][] multiArray) {
        for (int y = 0; y < multiArray.length; y++) {
            System.out.println();
            for (int x = 0; x < multiArray[y].length; x++) {
                System.out.print("[" + String.format("%02d", (int) multiArray[x][y]) + "]");
            }
        }
        System.out.println();
    }

    public static void printDoubleArray(double[][] multiArray) {
        for (int y = 0; y < multiArray.length; y++) {
            System.out.println();
            for (int x = 0; x < multiArray[y].length; x++) {
                System.out.print("[" + multiArray[x][y] + "]");
            }
        }
        System.out.println();
    }

    public static void printArray(int[][] multiArray) {
        for (int y = 0; y < multiArray.length; y++) {
            System.out.println();
            for (int x = 0; x < multiArray[y].length; x++) {
                System.out.print("[" + String.format("%02d", (int) multiArray[x][y]) + "]");
            }
        }
        System.out.println();
    }

    public static void printArrayFaceUp(int[][] multiArray) {
        for (int y = multiArray.length - 1; y >= 0; y--) {
            System.out.println();
            for (int x = 0; x < multiArray[y].length; x++) {
                System.out.print("[" + String.format("%02d", (int) multiArray[x][y]) + "]");
            }
        }
        System.out.println();
    }

    public static void printArrayFaceUp(double[][] multiArray) {
        for (int y = multiArray.length - 1; y >= 0; y--) {
            System.out.println();
            for (int x = 0; x < multiArray[y].length; x++) {
                System.out.print("[" + String.format("%02d", (int) multiArray[x][y]) + "]");
            }
        }
        System.out.println();
    }
}
