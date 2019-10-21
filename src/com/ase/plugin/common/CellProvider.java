package com.ase.plugin.common;

public interface CellProvider {

    String getCellTitle(int index);

    void setValueAt(int column, String text);
}
