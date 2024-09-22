package io.github.giulong.spectrum.it.data;

import lombok.Getter;

import java.util.Map;

@Getter
@SuppressWarnings("unused")
public class Data {

    private Map<String, User> users;
    private String checkboxEndpoint;
    private String flashMessageId;

    @Getter
    public static class User {
        private String name;
        private String password;
    }
}
