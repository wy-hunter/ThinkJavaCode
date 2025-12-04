import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.JFrame;

/**
 * Conway's Game of Life.
 * 
 * @author Chris Mayfield
 * @version 7.1.0
 */
public class Conway {

    private GridCanvas grid;

    /**
     * Creates a grid with two Blinkers.
     */
    public Conway() {
        grid = new GridCanvas(5, 10, 20);
        grid.turnOn(2, 1);
        grid.turnOn(2, 2);
        grid.turnOn(2, 3);
        grid.turnOn(1, 7);
        grid.turnOn(2, 7);
        grid.turnOn(3, 7);
    }

    public Conway(String path) { // Exercise 3 and 4
        int row = 0, col = 0, index = 0, line = 0;
        boolean bandaid = false; // for position 0,0
        int[][] list = new int[100][2];
        File file = new File(path);
        String[] fileType = path.split("[.]");
        String data;
        try (Scanner scan = new Scanner(file)) {
            if (fileType[1].equals("cells")) {
                data = scan.nextLine(); // Skip line 1
                while (scan.hasNextLine()) {
                data = scan.nextLine();
                for (int i = 0; i < data.length(); ++i) {
                    if (data.charAt(i) == 'O') {
                        if (row == 0 && i == 0) bandaid = true;
                        list[index][0] = row;
                        list[index][1] = i;
                        index += 1;
                    }
                    if (row == 0) col = data.length();
                }
                    row += 1;
                }
                grid = new GridCanvas(row, col, 20);
                for (int[] i: list) {
                    if (i[0] != 0 && i[1] != 0) {
                        grid.turnOn(i[0], i[1]);
                    } else {
                        if (bandaid) grid.turnOn(0,0);
                    }
                }
            }
            if (fileType[1].equals("rle")) {
                while (scan.hasNextLine()) {
                    data = scan.nextLine();
                    if (data.charAt(0) != '#') {
                        if (line == 0) { // First line
                            String[] init = data.replace(',', ' ').split("[ ]");
                            grid = new GridCanvas(Integer.parseInt(init[6]), Integer.parseInt(init[2]), 20);
                        } else { // Other lines
                            for (int i = 0; i < data.length(); ++i) {
                                // This implementation ignores numbers because I am reading the assignment and reference and
                                // neither explain what the number characters in the file mean so I am ignoring it.
                                if (data.charAt(i) == '$') { 
                                    row += 1; 
                                    col = 0; 
                                }
                                if (data.charAt(i) == 'o') grid.turnOn(row, col);
                                if (data.charAt(i) == 'b' || data.charAt(i) == 'o') col += 1;
                                if (data.charAt(i) == '!') break;
                            }
                        }
                        line += 1;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Counts the number of live neighbors around a cell.
     * 
     * @param r row index
     * @param c column index
     * @return number of live neighbors
     */
    private int countAlive(int r, int c) {
        int count = 0;
        count += grid.test(r - 1, c - 1);
        count += grid.test(r - 1, c);
        count += grid.test(r - 1, c + 1);
        count += grid.test(r, c - 1);
        count += grid.test(r, c + 1);
        count += grid.test(r + 1, c - 1);
        count += grid.test(r + 1, c);
        count += grid.test(r + 1, c + 1);
        return count;
    }

    /**
     * Apply the update rules of Conway's Game of Life.
     * 
     * @param cell the cell to update
     * @param count number of live neighbors
     */
    private static void updateCell(Cell cell, int count) {
        if (cell.isOn()) {
            if (count < 2 || count > 3) {
                // Any live cell with fewer than two live neighbors dies,
                // as if by underpopulation.
                // Any live cell with more than three live neighbors dies,
                // as if by overpopulation.
                cell.turnOff();
            }
        } else {
            if (count == 3) {
                // Any dead cell with exactly three live neighbors
                // becomes a live cell, as if by reproduction.
                cell.turnOn();
            }
        }
    }

    /**
     * Counts the neighbors before changing anything.
     * 
     * @return number of neighbors for each cell
     */
    private int[][] countNeighbors() {
        int rows = grid.numRows();
        int cols = grid.numCols();

        int[][] counts = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                counts[r][c] = countAlive(r, c);
            }
        }
        return counts;
    }

    /**
     * Updates each cell based on neighbor counts.
     * 
     * @param counts number of neighbors for each cell
     */
    private void updateGrid(int[][] counts) {
        int rows = grid.numRows();
        int cols = grid.numCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid.getCell(r, c);
                updateCell(cell, counts[r][c]);
            }
        }
    }

    /**
     * Simulates one round of Conway's Game of Life.
     */
    public void update() {
        int[][] counts = countNeighbors();
        updateGrid(counts);
    }

    /**
     * The simulation loop.
     */
    private void mainloop() {
        while (true) {

            // update the drawing
            this.update();
            grid.repaint();

            // delay the simulation
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

    /**
     * Creates and runs the simulation.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        String title = "Conway's Game of Life";
        //Conway game = new Conway();
        Conway game = new Conway("glider.rle");
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(game.grid);
        frame.pack();
        frame.setVisible(true);
        game.mainloop();
    }

}
