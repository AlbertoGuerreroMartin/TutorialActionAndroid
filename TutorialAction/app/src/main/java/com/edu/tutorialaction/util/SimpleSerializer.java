package com.edu.tutorialaction.util;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Utility
 *
 * @param <T> Type of the model
 */
public final class SimpleSerializer<T> {
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private static final Gson PRETTY_GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

    private final Class<T> clazz;
    private final TypeToken<T> typeToken;

    public SimpleSerializer(@NonNull Class<T> clazz) {
        this.clazz = clazz;
        this.typeToken = null;
    }

    public SimpleSerializer(@NonNull TypeToken<T> typeToken) {
        this.typeToken = typeToken;
        this.clazz = null;
    }

    /* JSON */
    public T fromJson(@NonNull String json) {
        if (clazz != null) {
            return GSON.fromJson(json, clazz);
        } else {
            return GSON.fromJson(json, typeToken.getType());
        }
    }

    public String toJson(@NonNull T object) {
        if (clazz != null) {
            return GSON.toJson(object, clazz);
        } else {
            return GSON.toJson(object, typeToken.getType());
        }
    }

    public String toPrettyJson(@NonNull T object) {
        if (clazz != null) {
            return PRETTY_GSON.toJson(object, clazz);
        } else {
            return PRETTY_GSON.toJson(object, typeToken.getType());
        }
    }

    /* BUNDLE */
    public T fromBundle(@NonNull Bundle bundle, @NonNull String key) {
        if (!bundle.containsKey(key)) return null;
        String json = bundle.getString(key);
        return fromJson(json);
    }

    /* OTHER METHODS */
    public T clone(@NonNull T object) {
        return fromJson(toJson(object));
    }
}