package com.ase.plugin.finder;

public class JavaFieldFinder extends AbsFieldFinder {
    @Override
    Boolean isDefaultChecked(String it) {
        if (it != null && it.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String transformToString(String it) {
        return it.replace("\"", "");
    }

    @Override
    protected String regex() {
        return "\".*?[^\\\\]\"";
    }
}
