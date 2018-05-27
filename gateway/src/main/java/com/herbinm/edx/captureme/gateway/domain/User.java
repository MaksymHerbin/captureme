package com.herbinm.edx.captureme.gateway.domain;

public class User {
    private final String nickname;

    public User(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
