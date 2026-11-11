package com.tarasov.tic_tac_toe.webSocket;

import lombok.Getter;
import lombok.Setter;


public class Greeting {
    private String content;

    public Greeting() {
    }

    public Greeting(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}