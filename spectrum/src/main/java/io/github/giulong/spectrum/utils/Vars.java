package io.github.giulong.spectrum.utils;

import static lombok.AccessLevel.PRIVATE;

import java.util.HashMap;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class Vars extends HashMap<String, String> {

    private static final Vars INSTANCE = new Vars();

    public static Vars getInstance() {
        return INSTANCE;
    }
}
