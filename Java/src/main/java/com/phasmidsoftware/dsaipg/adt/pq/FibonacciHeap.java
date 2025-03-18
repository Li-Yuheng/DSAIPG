package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.*;

public class FibonacciHeap<K> implements Iterable<K> {
    private Node<K> minNode;
    private int size;
    private final boolean max;
    private final Comparator<K> comparator;
    private final int maxSize;
    private Queue<K> overflowQueue = new LinkedList<>();
    private K highestOverflowElement = null;

    private static class Node<K> {
        K key;
        Node<K> parent;
        Node<K> child;
        Node<K> left;
        Node<K> right;
        int degree;
        boolean mark;

        Node(K key) {
            this.key = key;
            this.right = this;
            this.left = this;
        }
    }

    public FibonacciHeap(boolean max, Comparator<K> comparator, int maxSize) {
        this.max = max;
        this.comparator = comparator;
        this.minNode = null;
        this.size = 0;
        this.maxSize = maxSize;
    }

    public boolean isEmpty() {
        return minNode == null;
    }

    public int size() {
        return size;
    }

    public void insert(K key) {
        if (size >= maxSize) {
            overflowQueue.add(key);
            updateHighestOverflowElement(key);
            return;
        }
        Node<K> newNode = new Node<>(key);
        minNode = mergeLists(minNode, newNode);
        if (minNode == null || compare(key, minNode.key) < 0) {
            minNode = newNode;
        }
        size++;
    }

    private void updateHighestOverflowElement(K element) {
        if (highestOverflowElement == null || comparator.compare(element, highestOverflowElement) > 0) {
            highestOverflowElement = element;
        }
    }

    public K getHighestOverflowElement() {
        return highestOverflowElement;
    }

    public String getOverflowElements() {
        StringBuilder sb = new StringBuilder();
        sb.append("Overflow elements:");
        for (K element : overflowQueue) {
            sb.append("\t").append(element);
        }
        return sb.toString();
    }

    public K findMin() {
        if (minNode == null) throw new NoSuchElementException("Heap is empty");
        return minNode.key;
    }

    public K deleteMin() {
        if (minNode == null) throw new NoSuchElementException("Heap is empty");

        Node<K> z = minNode;
        if (z.child != null) {
            Node<K> child = z.child;
            Node<K> start = child;
            do {
                child.parent = null;
                child = child.right;
            } while (child != start);
            minNode = mergeLists(minNode, z.child);
        }

        removeNode(z);
        size--;

        if (z == z.right) {
            minNode = null;
        } else {
            minNode = z.right;
            consolidate();
        }

        return z.key;
    }

    private void removeNode(Node<K> node) {
        if (node.right == node) {
            return;
        }
        node.left.right = node.right;
        node.right.left = node.left;
    }

    private Node<K> mergeLists(Node<K> a, Node<K> b) {
        if (a == null) return b;
        if (b == null) return a;

        Node<K> tempA = a.right;
        Node<K> tempB = b.right;
        a.right = tempB;
        tempB.left = a;
        b.right = tempA;
        tempA.left = b;

        return compare(a.key, b.key) < 0 ? a : b;
    }

    private void consolidate() {
        Map<Integer, Node<K>> degreeTable = new HashMap<>();
        List<Node<K>> rootList = new ArrayList<>();
        Node<K> current = minNode;
        if (current != null) {
            do {
                rootList.add(current);
                current = current.right;
            } while (current != minNode);

            for (Node<K> node : rootList) {
                int degree = node.degree;
                while (degreeTable.containsKey(degree)) {
                    Node<K> other = degreeTable.get(degree);
                    if (compare(node.key, other.key) > 0) {
                        Node<K> temp = node;
                        node = other;
                        other = temp;
                    }
                    link(other, node);
                    degreeTable.remove(degree);
                    degree++;
                }
                degreeTable.put(degree, node);
            }
        }

        minNode = null;
        for (Node<K> node : degreeTable.values()) {
            if (minNode == null || compare(node.key, minNode.key) < 0) {
                minNode = node;
            }
        }
    }

    private void link(Node<K> y, Node<K> x) {
        removeNode(y);
        y.right = y;
        y.left = y;
        if (x.child == null) {
            x.child = y;
        } else {
            x.child = mergeLists(x.child, y);
        }
        y.parent = x;
        x.degree++;
        y.mark = false;
    }

    private int compare(K a, K b) {
        return max ? comparator.compare(b, a) : comparator.compare(a, b);
    }

    @Override
    public Iterator<K> iterator() {
        return new Iterator<K>() {
            private final Queue<Node<K>> queue = new LinkedList<>();

            {
                if (minNode != null) {
                    Node<K> current = minNode;
                    do {
                        queue.add(current);
                        current = current.right;
                    } while (current != minNode);
                }
            }

            @Override
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            @Override
            public K next() {
                if (!hasNext()) throw new NoSuchElementException();
                return queue.poll().key;
            }
        };
    }
}
