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
//        return it.replace("\"", "");
        return it.replace("R.string.", "");
    }

    @Override
    protected String regex() {
//        return "\".*?[^\\\\]\"";
//        return "AppProfile\\.getContext\\(\\)\\.getString\\(R\\.string\\..{64,}\\)";
        return "R\\.string\\.[A-Za-z0-9_]{64,}";
    }
}
