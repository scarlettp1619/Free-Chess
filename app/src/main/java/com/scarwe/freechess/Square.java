package com.scarwe.freechess;

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

    public void setCol(int col) { this.col = col; }
}