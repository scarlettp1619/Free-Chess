package com.scarwe.freechess;

public class Square {
    int row, col;
    public Square(int col, int row) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
