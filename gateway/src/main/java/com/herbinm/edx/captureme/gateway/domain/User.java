package com.herbinm.edx.captureme.gateway.domain;

public class User {

    private final String nickname;
    private final String uniqueId;

    public User(String nickname, String uniqueId) {
        this.nickname = nickname;
        this.uniqueId = uniqueId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUniqueId() {
        return uniqueId;
    }
}
