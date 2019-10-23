package com.ase.plugin.finder;

public class LayoutXmlFieldFinder extends AbsFieldFinder {
    @Override
    Boolean isDefaultChecked(String it) {
        return !it.contains("@string/");
    }

    @Override
    protected String transformToString(String it) {
//        String result = it.replace("android:text=\"", "");
        String result = it.replace("@string/", "");
        return result.replace("\"", "");
    }

    @Override
    protected String regex() {
//        return "android:text=\".*?\"";
        return "\"@string/.{64,}\"";
    }
}
