package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Neutron on 7/28/2017.
 */
public class Field {
    boolean[][] mine;
    boolean[][] visible;
    boolean[][] flagged;
    int x;
    int y;
    boolean showMines;
    int numMines;
    int flags;

    public Field(int X, int Y, int Width, int Height, int numMines, int startX, int startY) {
        //Instantiate x and y.
        x = X;
        y = Y;
        this.numMines = numMines;
        //Instantiate mine.
        mine = new boolean[Width][Height];
        visible = new boolean[Width][Height];
        flagged = new boolean[Width][Height];
        //Create a Random.
        Random r = new Random();
        //Initialize mine, visible, and flagged so that they are full of falses.
        for(int i = 0; i < Width; i++)
            for(int j = 0; j < Height; j++) {
                mine[i][j] = false;
                visible[i][j] = false;
                flagged[i][j] = false;
            }
        //Until you have created the desired number of mines,
        I:
        for(int I = 0; I < numMines; I++) {
            //Randomly select a pair of coordinates.
            int x = r.nextInt(Width);
            int y = r.nextInt(Height);
            //Do not place a mine here if this is the start position, or next to the start position.
            for(int i = (x != 0 ? -1 : 0); i <= (x != mine.length - 1 ? 1 : 0); i++)
                for(int j = (y != 0 ? -1 : 0); j <= (y != mine[0].length - 1 ? 1 : 0); j++)
                    if(x == startX + i && y == startY + j) {
                        //Decrement I so that the loop will add one iteration to compensate for not placing a mine in this one.
                        I--;
                        continue I;
                    }
            //If there isn't a mine here,
            if(!mine[x][y])
                //place one.
                mine[x][y] = true;
                //Otherwise,
            else
                //Decrement I so that the loop will add one iteration to compensate for not placing a mine in this one.
                I--;
            //If the field is full of mines,
            if(I == Width * Height - 9)
                //break out of the loop.
                break;
        }

    }

    public void sweep(int x, int y) {
        if(flagged[x][y])
            return;
        if(visible[x][y]) {
            if(number(x, y) == numberFlagged(x, y) && number(x, y) != 0)
                revealNeighbors(x, y, true);
            return;
        }
        reveal(x, y);
    }


    //Used to reveal a tile.
    public void reveal(int x, int y, boolean safety) {
        //Do not allow flagged tiles to be revealed if safety is used.
        if(flagged[x][y] && safety)
            return;
        //If this tile is visible,
        if(visible[x][y]) {
            //do not continue on with this method.
            return;
        }
        //If this tile is a mine,
        if(mine[x][y])
            //you lose.
            showMines();
            //If not,
        else {
            //make this tile visible, and,
            show(x, y);
            //reveal all neighbors that are zeros, and,
            revealZeroNeighbors(x, y);
            //if there are no mines next to this tile,
            if(number(x, y) == 0)
                //reveal all neighbors of this tile, flagged or otherwise.
                revealNeighbors(x, y);
        }
    }

    public void reveal(int x, int y) {
        reveal(x, y, true);
    }

    private void revealZeroNeighbors(int x, int y) {
        for(int i = (x != 0 ? -1 : 0); i <= (x != mine.length - 1 ? 1 : 0); i++)
            for(int j = (y != 0 ? -1 : 0); j <= (y != mine[0].length - 1 ? 1 : 0); j++)
                if(number(x + i, y + j) == 0)
                    reveal(x + i, y + j, false);
    }

    private void revealNeighbors(int x, int y) {
        revealNeighbors(x, y, false);
    }

    private void revealNeighbors(int x, int y, boolean safety) {
        for(int i = (x != 0 ? -1 : 0); i <= (x != mine.length - 1 ? 1 : 0); i++)
            for(int j = (y != 0 ? -1 : 0); j <= (y != mine[0].length - 1 ? 1 : 0); j++)
                reveal(x + i, y + j, safety);
    }

    private void show(int x, int y) {
        visible[x][y] = true;
        flagged[x][y] = false;
    }

    public void showMines() {
        showMines = true;
    }

    public int numberFlagged(int x, int y) {
        int $ = 0;
        if(!visible[x][y])
            return -1;
        else
            for(int i = (x != 0 ? -1 : 0); i <= (x != mine.length - 1 ? 1 : 0); i++)
                for(int j = (y != 0 ? -1 : 0); j <= (y != mine[0].length - 1 ? 1 : 0); j++)
                    if(flagged[x + i][y + j])
                        $++;
        return $;
    }

    public int number(int x, int y) {
        int $ = 0;
        if(mine[x][y])
            return -1;
        else
            for(int i = (x != 0 ? -1 : 0); i <= (x != mine.length - 1 ? 1 : 0); i++)
                for(int j = (y != 0 ? -1 : 0); j <= (y != mine[0].length - 1 ? 1 : 0); j++)
                    if(mine[x + i][y + j])
                        $++;
        return $;
    }

    public int getMines() {
        return numMines - getFlags();
    }

    public boolean lost() {
        return showMines;
    }

    public void flag(int x, int y) {
        if(flagged[x][y]) {
            flagged[x][y] = false;

            return;
        }
        if(!visible[x][y]) {
            flagged[x][y] = true;

        }
    }

    public int getFlags() {
        int $ = 0;
        for(int i = 0; i < mine.length; i++)
            for(int j = 0; j < mine[0].length; j++)
                if(flagged[i][j])
                    $++;
        return $;
    }

    public void confusion(int x, int y) {
        if(mine[x][y] && conundrum())
            flag(x, y);
        else
            sweep(x, y);
    }

    public boolean won(){
        for(int i = 0; i < mine.length; i++)
            for(int j = 0; j < mine[0].length; j++)
                if(!visible[i][j] && !flagged[i][j])
                    return false;
        return true;
    }

    public boolean conundrum() {
        ArrayList<Integer> px = new ArrayList<>();
        ArrayList<Integer> py = new ArrayList<>();
        for(int i = 0; i < mine.length; i++)
            for(int j = 0; j < mine[0].length; j++)
                if(numberVisible(i, j) > 0) {
                    px.add(i);
                    py.add(j);
                }
        int realBin = 0;
        for(int i = 0; i < px.size(); i++){
            if(mine[px.get(i)][py.get(i)])
                realBin += 1<<i;
        }
        final int maxBin;
        {
            int tempMaxBin = 0;
            for(int i = 0; i < px.size(); i++)
                tempMaxBin += 1<<i;
            maxBin = tempMaxBin;
        }
        ArrayList<Integer> ix = new ArrayList<>();
        ArrayList<Integer> iy = new ArrayList<>();
        ArrayList<Integer> ic = new ArrayList<>();
        ArrayList<ArrayList<Integer>> ir = new ArrayList<>();
        for(int i = 0; i < mine.length; i++)
            for(int j = 0; j < mine[0].length; j++)
                if(visible[i][j] && number(i, j) > 0){
                    ix.add(i);
                    iy.add(j);
                    ic.add(number(i, j));
                    ArrayList<Integer> r = new ArrayList<>();
                    for(int k = 0; k < px.size(); k++)
                        for(int l = (x != 0 ? -1 : 0); l <= (x != mine.length - 1 ? 1 : 0); l++)
                            for(int m = (y != 0 ? -1 : 0); m <= (y != mine[0].length - 1 ? 1 : 0); m++)
                                if(i == px.get(k) + l && j == py.get(k) + m)
                                    r.add(k);
                    ir.add(r);
                }
        ArrayList<Integer> simBins = new ArrayList<>();
        bin: for(int bin = 0; bin < maxBin; bin++) {
            for(int i = 0; i < ix.size(); i++)
                for(int k : ir.get(i))
                    if(ic.get(i) != (bin & (1 << k)))
                        continue bin;
            simBins.add(bin);
        }
        int orBins = 0;
        if(simBins.size() == 0)
            System.out.println("Something went wrong, there aren't any simBins!");
        for(int b : simBins)
            orBins |= b;
        System.out.println(orBins);
        if(simBins.size() < 1)
            return false;
        return orBins == maxBin;
    }

    public int numberVisible(int x, int y) {
        int $ = 0;
        for(int i = (x != 0 ? -1 : 0); i <= (x != mine.length - 1 ? 1 : 0); i++)
            for(int j = (y != 0 ? -1 : 0); j <= (y != mine[0].length - 1 ? 1 : 0); j++)
                if(visible[x + i][y + j])
                    $++;
        return $;
    }

    public void Paint(GraphicsContext g) {
        //For every tile:
        for(int i = 0; i < mine.length; i++)
            for(int j = 0; j < mine[0].length; j++) {
                //If this tile is visible,
                if(visible[i][j] || (showMines && mine[i][j] && !flagged[i][j]))
                    //set the color to light grey.
                    g.setFill(new Color(.7, .7, .7, 1));
                    //Otherwise,
                else
                    //set the color to dark grey.
                    g.setFill(new Color(.5, .5, .5, 1));
                //Fill this tile with whatever color you picked.
                g.fillRect(x + i * 16, y + j * 16, 16, 16);
                if(visible[i][j])
                    switch(number(i, j)) {
                        case 0:
                            break;
                        case 1:
                            g.setStroke(new Color(0, 0, 1, 1));
                            g.strokeText("1", x + i * 16 + 4, y + j * 16 + 12);
                            break;
                        case 2:
                            g.setStroke(new Color(0, .5, 0, 1));
                            g.strokeText("2", x + i * 16 + 4, y + j * 16 + 12);
                            break;
                        case 3:
                            g.setStroke(new Color(1, 0, 0, 1));
                            g.strokeText("3", x + i * 16 + 4, y + j * 16 + 12);
                            break;
                        case 4:
                            g.setStroke(new Color(0, 0, .5, 1));
                            g.strokeText("4", x + i * 16 + 4, y + j * 16 + 12);
                            break;
                        case 5:
                            g.setStroke(new Color(.5, 0, 0, 1));
                            g.strokeText("5", x + i * 16 + 4, y + j * 16 + 12);
                            break;
                        case 6:
                            g.setStroke(new Color(0, .5, .5, 1));
                            g.strokeText("6", x + i * 16 + 4, y + j * 16 + 12);
                            break;
                        case 7:
                            g.setStroke(new Color(0, 0, 0, 1));
                            g.strokeText("7", x + i * 16 + 4, y + j * 16 + 12);
                            break;
                        case 8:
                            g.setStroke(new Color(.5, .5, .5, 1));
                            g.strokeText("8", x + i * 16 + 4, y + j * 16 + 12);
                            break;
                    }
                if(flagged[i][j]) {
                    g.setFill(new Color(0, 0, 0, 1));
                    g.fillRect(x + i * 16 + 8, y + j * 16 + 5, 2, 9);
                    g.fillRect(x + i * 16 + 4, y + j * 16 + 11, 8, 3);
                    g.setFill(new Color(1, 0, 0, 1));
                    if(showMines && !mine[i][j])
                        g.setFill(new Color(1, 1, 1, 1));
                    double[] xs = new double[3];
                    double[] ys = new double[3];
                    xs[0] = x + i * 16 + 3;
                    ys[0] = y + j * 16 + 5;
                    xs[1] = x + i * 16 + 10;
                    ys[1] = y + j * 16 + 2;
                    xs[2] = x + i * 16 + 10;
                    ys[2] = y + j * 16 + 8;
                    g.fillPolygon(xs, ys, 3);
                }
                if(showMines && mine[i][j] && !flagged[i][j]) {
                    g.setFill(new Color(0, 0, 0, 1));
                    g.setStroke(new Color(0, 0, 0, 1));
                    g.fillOval(x + i * 16 + 4, y + j * 16 + 4, 8, 8);
                    g.fillRect(x + i * 16 + 7, y + j * 16 + 2, 2, 12);
                    g.fillRect(x + i * 16 + 2, y + j * 16 + 7, 12, 2);
                    g.strokeLine(x + i * 16 + 4, y + j * 16 + 4, x + i * 16 + 12, y + j * 16 + 12);
                    g.strokeLine(x + i * 16 + 12, y + j * 16 + 4, x + i * 16 + 4, y + j * 16 + 12);
                }
                //Set the stroke color to black.
                g.setStroke(new Color(0, 0, 0, 1));
                //Stroke a square around this tile.
                g.strokeRect(x + i * 16, y + j * 16, 16, 16);
            }
    }
}