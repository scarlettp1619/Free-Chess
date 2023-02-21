package com.scarwe.freechess.game;

public class Square {
    int row, col;
    public Square(int col, int row) {
        this.row = row;
        this.col = col;
    }

    public int getCol(){
        return col;
    }

    public int getRow() {
        return row;
    }

    // should probably be in a different class but is used for notation
    public String getValToString(int i)  {
        int j = i + 1;
        return j > 0 && j < 27 ? String.valueOf((char)(j+64)) : null;
    }

}
