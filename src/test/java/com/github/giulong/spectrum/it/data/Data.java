package com.github.giulong.spectrum.it.data;

import lombok.Getter;

import java.util.Map;

@Getter
public class Data {

    private Map<String, User> users;

    @Getter
    public static class User {
        private String name;
        private String email;
        private String password;
        private String company;
        private String website;
        private String country;
        private String city;
        private String address1;
        private String address2;
        private String state;
        private String zipCode;
    }
}
