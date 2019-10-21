package com.ase.plugin.entity;


import com.ase.plugin.common.CellProvider;
import com.ase.plugin.common.Selector;

public class FieldEntity implements Selector, CellProvider {
    public String source = "";
    public String result = "";
    public Boolean isSelected = true;
    public String resultSrc = "";

    public FieldEntity(String source, String result, Boolean isSelected) {
        this.source = source;
        this.result = result;
        this.isSelected = isSelected;
    }

    @Override
    public String getCellTitle(int index) {
        switch (index) {
            case 0:
                return source;
            case 1:
                return result;
            default:
                return "";
        }
    }

    @Override
    public void setValueAt(int column, String text) {
        switch (column) {
            case 0:
                source = (text != null) ? text : "";
                break;
            case 1:
                result = (text != null) ? text : "";
                break;
        }
    }

    @Override
    public void setSelect(boolean select) {
        this.isSelected = select;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + source.hashCode();
        result = result * 31 + result;

        return result;
    }

    // 使用 result 值判定相等，用于在 StringsWriter 类中使用 Set 去重
    @Override
    public boolean equals(Object other) {
        if(other == this) return true;
        if(!(other instanceof FieldEntity)) return false;

        FieldEntity o = (FieldEntity)other;
        return o.source.equals(source);
    }
}
