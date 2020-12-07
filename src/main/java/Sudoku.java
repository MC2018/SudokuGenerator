import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author MC_2018
 */
public class Sudoku {

    private Random random = new Random();
    private int[][] sudokuNumbers = new int[9][9];
    private long iterations = 0;
    private long totalIterations = 0;
    private boolean diagonals;
    private int magicNumber = 180;

    public Sudoku() {
        for (int x = 0; x < sudokuNumbers.length; x++) {
            for (int y = 0; y < sudokuNumbers.length; y++) {
                sudokuNumbers[x][y] = 0;
            }
        }
    }

    public Sudoku(int seed) {
        this();
        random = new Random(seed);
    }

    public Sudoku(boolean diagonals) {
        this();
        this.diagonals = diagonals;
    }

    public Sudoku(int seed, boolean diagonals) {
        this();
        random = new Random(seed);
        this.diagonals = diagonals;
    }

    public Sudoku(boolean diagonals, int seed) {
        this(seed, diagonals);
    }

    public int removeNumbers(int amount) {
        ArrayList<Integer> indices = new ArrayList();
        int removedNumbers = 0;

        for (int i = 0; i < 81; i++) {
            indices.add(i);
        }

        for (int i = 0; removedNumbers < amount && indices.size() > 0; i++) {
            int index = indices.remove(random.nextInt(indices.size()));
            int x = index / 9;
            int y = index % 9;

            if (rowSafeToRemove(x, y) || columnSafeToRemove(x, y) || squareSafeToRemove(x, y)) {
                sudokuNumbers[x][y] = 0;
                removedNumbers++;
            }
        }

        return removedNumbers;
    }

    private int sameVertical(int[][] sudokuNumbers, int squareR, int squareC) {
        int verticalX = -1;

        for (int c = 0; c < 3; c++) {
            for (int r = 0; r < 3; r++) {
                if (sudokuNumbers[squareR * 3 + r][squareC * 3 + c] == 0 && verticalX == -1) {
                    verticalX = squareR * 3 + r;
                } else if (sudokuNumbers[squareR * 3 + r][squareC * 3 + c] == 0 && verticalX != squareR * 3 + r) {
                    return -1;
                }
            }
        }

        return verticalX;
    }

    private boolean squareContains(int number, int[][] sudokuNumbers, int squareC, int squareR) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (sudokuNumbers[squareR * 3 + x][squareC * 3 + y] == number) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean rowSafeToRemove(int x, int y) {
        for (int r = 0; r < sudokuNumbers.length; r++) {
            r = r == x ? r + 1 : r;

            if (r >= sudokuNumbers.length) {
                return true;
            }

            if (sudokuNumbers[r][y] < 1) {
                boolean found = false;

                for (int c = 0; c < sudokuNumbers.length; c++) {
                    if (sudokuNumbers[r][c] == sudokuNumbers[x][y]) {
                        c = sudokuNumbers.length - 1;
                        found = true;
                    }
                }

                if (!found) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean squareSafeToRemove(int x, int y) {
        int[][] sudokuNumbers = new int[9][9];
        int selectedNumber = this.sudokuNumbers[x][y];
        int selectedNumberR = x / 3;
        int selectedNumberC = y / 3;
        int impossibleSpaces = 0;

        for (int i = 0; i < 9; i++) {
            System.arraycopy(this.sudokuNumbers[i], 0, sudokuNumbers[i], 0, 9);
        }

        for (int c = 0; c < 9; c++) {
            for (int r = 0; r < 9; r++) {
                if (sudokuNumbers[r][c] > 0 && sudokuNumbers[r][c] != selectedNumber) {
                    sudokuNumbers[r][c] = -1;
                } else if (sudokuNumbers[r][c] == selectedNumber && (x != r || y != c)) {
                    for (int i = 0; i < 9; i++) {
                        sudokuNumbers[r][i] = i == c ? selectedNumber : -1;
                        sudokuNumbers[i][c] = i == r ? selectedNumber : -1;
                    }
                }
            }
        }

        for (int squareC = 0; squareC < 3; squareC++) {
            for (int squareR = 0; squareR < 3; squareR++) {
                if (!squareContains(selectedNumber, sudokuNumbers, squareR, squareC)) {
                    int horizontalY = sameHorizontal(sudokuNumbers, squareR, squareC);
                    int verticalX = sameVertical(sudokuNumbers, squareR, squareC);

                    if (horizontalY != -1) {
                        for (int i = 0; i < 9; i++) {
                            sudokuNumbers[i][horizontalY] = -1;
                        }
                    } else if (verticalX != -1) {
                        for (int i = 0; i < 9; i++) {
                            sudokuNumbers[verticalX][i] = -1;
                        }
                    }
                }
            }
        }

        for (int c = selectedNumberC * 3; c < selectedNumberC * 3 + 3; c++) {
            for (int r = selectedNumberR * 3; r < selectedNumberR * 3 + 3; r++) {
                if (sudokuNumbers[r][c] == -1) {
                    impossibleSpaces++;
                }
            }
        }

        return impossibleSpaces == 8;
    }

    private int sameHorizontal(int[][] sudokuNumbers, int squareR, int squareC) {
        int horizontalY = -1;

        for (int c = 0; c < 3; c++) {
            for (int r = 0; r < 3; r++) {
                if (sudokuNumbers[squareR * 3 + r][squareC * 3 + c] == 0 && horizontalY == -1) {
                    horizontalY = squareC * 3 + c;
                } else if (sudokuNumbers[squareR * 3 + r][squareC * 3 + c] == 0 && horizontalY != squareC * 3 + c) {
                    return -1;
                }
            }
        }

        return horizontalY;
    }

    private boolean columnSafeToRemove(int x, int y) {
        for (int c = 0; c < sudokuNumbers.length; c++) {
            if (c == y) {
                c++;
            }

            if (c >= sudokuNumbers.length) {
                return true;
            }

            if (sudokuNumbers[x][c] < 1) {
                boolean found = false;

                for (int r = 0; r < sudokuNumbers.length; r++) {
                    if (sudokuNumbers[r][c] == sudokuNumbers[x][y]) {
                        r = sudokuNumbers.length - 1;
                        found = true;
                    }
                }

                if (!found) {
                    return false;
                }
            }
        }

        return true;
    }

    //add something for diagonal generations in which the program will attempt to select a number that has not yet been used in the diagonal(s) that it is working on
    //it could cross-examine information to find out what both lines need, and if it can't find a valid match, it will either reset the board or reset to the beginning of previous line
    public void createRows() {
        int[] setList = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        iterations = 0;

        for (int y = 0; y < sudokuNumbers.length; y++) {
            int[] list = setList;
            int generations = 0;

            for (int x = 0; x < sudokuNumbers[y].length; x++) {
                int index = random.nextInt(list.length);
                generations++;
                iterations++;

                if (!repeatsInSquare(x, y, list[index]) && !repeatsInColumn(x, list[index]) && (!diagonals || !repeatsInDiagonals(x, y, list[index]))) {
                    sudokuNumbers[x][y] = list[index];
                    list = remove(list, index);
                } else {
                    x--;
                }

                if (generations > magicNumber) {
                    generations = 0;
                    clear(y);

                    x = -1;
                    y = y == 0 ? 0 : y - 1;

                    list = setList;
                } else if ((!diagonals && iterations % 10000 == 0) || (diagonals && iterations % 500000000 == 0)) {
                    for (int i = 0; i < sudokuNumbers.length; i++) {
                        clear(i);
                    }

                    x = -1;
                    y = 0;

                    list = setList;
                }
            }
        }

        totalIterations += iterations;
    }

    public void createRows(boolean diagonals) {
        boolean originalDiagonals = this.diagonals;
        this.diagonals = diagonals;
        createRows();
        this.diagonals = originalDiagonals;
    }

    private void clear(int y) {
        for (int[] sudokuNumber : sudokuNumbers) {
            sudokuNumber[y] = 0;
        }
    }

    private boolean repeatsInSquare(int x, int y, int number) {
        int xOffset = x / 3 * 3;
        int yOffset = y / 3 * 3;

        for (int c = 0; c < 3; c++) {
            for (int r = 0; r < 3; r++) {
                if (sudokuNumbers[r + xOffset][c + yOffset] == number) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean repeatsInColumn(int xCoord, int number) {
        for (int y = 0; y < sudokuNumbers.length; y++) {
            if (sudokuNumbers[xCoord][y] == number) {
                return true;
            }
        }

        return false;
    }

    private boolean repeatsInDiagonals(int x, int y, int number) {
        String startingCorner;

        if (x == y) {
            startingCorner = "left";
        } else if (x + y == 8) {
            startingCorner = "right";
        } else {
            return false;
        }

        if (x == y && x + y == 8) {
            startingCorner = "leftright";
        }


        if (startingCorner.contains("left")) {
            for (int i = 0; i < y; i++) {
                if (sudokuNumbers[i][i] == number) {
                    return true;
                }
            }
        }

        if (startingCorner.contains("right")) {
            for (int i = 0; i < y; i++) {
                if (sudokuNumbers[8 - i][i] == number) {
                    return true;
                }
            }
        }

        return false;
    }

    private static int[] remove(int[] value, int index) {
        int[] newValue = new int[value.length - 1];
        int newIndex = 0;

        if (value.length == 1) {
            return new int[0];
        }

        for (int i = 0; i < value.length; i++) {
            if (i != index) {
                newValue[newIndex] = value[i];
                newIndex++;
            }
        }

        return newValue;
    }

    public int[][] getSudokuNumbers() {
        int[][] sudokuNumbers = new int[9][9];

        for (int i = 0; i < 9; i++) {
            System.arraycopy(this.sudokuNumbers[i], 0, sudokuNumbers[i], 0, 9);
        }

        return sudokuNumbers;
    }

    public long getTotalIterations() {
        return totalIterations;
    }

    public void print(int[][] numbers) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (numbers[x][y] == 0) {
                    System.out.print("_ ");
                } else if (numbers[x][y] == -1) {
                    System.out.print("X ");
                } else {
                    System.out.print(numbers[x][y] + " ");
                }

                if (x % 3 == 2 && x != 8) {
                    System.out.print("  ");
                }
            }

            System.out.println();

            if (y % 3 == 2 && y != 8) {
                System.out.println();
            }
        }

        System.out.println("\n");
    }

}