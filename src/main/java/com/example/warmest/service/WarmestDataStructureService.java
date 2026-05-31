package com.example.warmest.service;

import com.example.warmest.core.WarmestDataStructureInterface;
import com.example.warmest.model.Node;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(
        name = "warmest.backend",
        havingValue = "memory",
        matchIfMissing = true
)
public class WarmestDataStructureService implements WarmestDataStructureInterface {
    private final Map<String, Node> nodesByKey = new HashMap<>();
    private final Node head;
    private final Node tail;

    public WarmestDataStructureService() {
        this.head = new Node(null, 0);
        this.tail = new Node(null, 0);

        this.head.setNext(tail);
        this.tail.setPrev(head);
    }

    @Override
    public synchronized Integer put(String key, int value) {
        validateKey(key);

        Node existing = nodesByKey.get(key);
        if (existing != null) {
            Integer oldValue = existing.getValue();
            existing.setValue(value);
            moveToHead(existing);
            return oldValue;
        }
        Node node = new Node(key, value);
        nodesByKey.put(key, node);
        addToHead(node);
        return null;
    }

    @Override
    public synchronized Integer remove(String key) {
        validateKey(key);

        Node node = nodesByKey.get(key);
        if (node == null)
            return null;

        removeNode(node);
        nodesByKey.remove(node.getKey());
        return node.getValue();
    }

    @Override
    public synchronized Integer get(String key) {
        validateKey(key);

        Node node = nodesByKey.get(key);
        if (node == null)
            return null;

        moveToHead(node);
        return node.getValue();
    }

    @Override
    public synchronized String getWarmest() {
        if (head.getNext() == tail)
            return null;
        return head.getNext().getKey();
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    private void addToHead(Node node) {
        node.setNext(head.getNext());
        node.setPrev(head);
        head.getNext().setPrev(node);
        head.setNext(node);
    }

    private void removeNode(Node node) {
        Node nextNode = node.getNext();
        Node prevNode = node.getPrev();

        nextNode.setPrev(prevNode);
        prevNode.setNext(nextNode);

        node.setNext(null);
        node.setPrev(null);
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be null or blank");
        }
    }
}
