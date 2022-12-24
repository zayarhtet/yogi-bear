package model;

public enum CellItem {
    BASKET('*'), WALL('#'), EMPTY(' '), H_RANGER('H'), V_RANGER('V');
    CellItem(char rep) { representation = rep; }
    public final char representation;
}
