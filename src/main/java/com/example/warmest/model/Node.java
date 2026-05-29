package com.example.warmest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Node {
    private final String key;

    @Setter
    private Integer value;
    @Setter
    private Node prev;
    @Setter
    private Node next;

    public Node(String key, Integer value) {
        this.key = key;
        this.value = value;
    }
}
