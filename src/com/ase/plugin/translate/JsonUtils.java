package com.ase.plugin.translate;

import a.g.T;
import aapt.pb.repackage.com.google.protobuf.Any;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JsonUtils {

    private static Gson gson = new Gson();

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }


    public String toJson(Any src) {
        return gson.toJson(src);
    }
}
