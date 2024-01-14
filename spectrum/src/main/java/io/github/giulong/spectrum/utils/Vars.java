package io.github.giulong.spectrum.utils;

import lombok.NoArgsConstructor;

import java.util.HashMap;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Vars extends HashMap<String, String> {

    private static final Vars INSTANCE = new Vars();

    public static Vars getInstance() {
        return INSTANCE;
    }
}
