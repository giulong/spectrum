package io.github.giulong.spectrum.it_testbook.data;

import java.util.Map;

import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public class Data {

    private Map<String, User> users;

    @Getter
    public static class User {
        private String name;
        private String password;
    }
}
