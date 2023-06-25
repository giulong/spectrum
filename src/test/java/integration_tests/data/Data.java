package integration_tests.data;

import lombok.Getter;

import java.util.Map;

@Getter
public class Data {

    private Map<String, User> users;

    @Getter
    public static class User {
        private String name;
        private String password;
    }
}
