package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * 4-Ary Heap Priority Queue Implementation
 * This data structure generalizes a binary heap, allowing each node to have up to four children.
 * Supports both Min-Heap and Max-Heap configurations.
 *
 * @param <K> The type of elements stored in the heap.
 */
public class FourAryHeap<K> implements Iterable<K> {
    private final boolean max;
    private final int first;
    private final Comparator<K> comparator;
    private final K[] heap;
    private int last;
    private final boolean floyd;
    private Queue<K> overflowQueue = new LinkedList<>();
    private K highestOverflowElement = null;
    /**
     * Primary constructor: Build a heap using an existing array.
     *
     * @param max        true for max-heap, false for min-heap
     * @param heap       existing array
     * @param first      index of root element
     * @param last       number of elements in heap
     * @param comparator comparator for type K
     * @param floyd      use Floyd's heap optimization (true or false)
     */

    public FourAryHeap(boolean max, Object[] heap, int first, int last, Comparator<K> comparator, boolean floyd) {
        this.max = max;
        this.first = first;
        this.comparator = comparator;
        this.last = last;
        this.floyd = floyd;
        this.heap = (K[]) heap;

    }

    /**
     * Constructor: Create an empty heap with given capacity.
     *
     * @param n          maximum capacity
     * @param first      root index
     * @param max        true for max-heap, false for min-heap
     * @param comparator comparator for ordering elements
     * @param floyd      use Floyd's optimization
     */
    public FourAryHeap(int n, int first, boolean max, Comparator<K> comparator, boolean floyd) {
        this(max, new Object[n + first], first, 0, comparator, floyd);
    }

    public FourAryHeap(int n, boolean max, Comparator<K> comparator, boolean floyd) {

        this(n, 1, max, comparator, floyd);
    }

    /**
     * Constructor: Create an empty heap with given capacity (default root index).
     *
     * @param n          maximum capacity
     * @param max        true for max-heap, false for min-heap
     * @param comparator comparator for ordering elements
     */
    public FourAryHeap(int n, boolean max, Comparator<K> comparator) {
        this(n, 1, max, comparator, false);
    }

    /**
     * Constructor: Create an empty max-heap with given capacity.
     *
     * @param n          maximum capacity
     * @param comparator comparator for ordering elements
     */
    public FourAryHeap(int n, Comparator<K> comparator) {
        this(n, 1, true, comparator, false);
    }


    public boolean isEmpty() {
        return last == 0;
    }

    public int size() {
        return last;
    }

    public void give(K key) {
        if (last == heap.length - first){
            K overflowElement = heap[last + first - 1];
            overflowQueue.add(overflowElement);
            updateHighestOverflowElement(overflowElement);
            last--;
        }
        heap[++last + first - 1] = key;
        swimUp(last + first - 1);
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

    public K take() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        if (floyd) return doTake(this::snake);
        else return doTake(this::sink);
    }

    private K doTake(Consumer<Integer> f) {
        K result = heap[first];
        swap(first, last-- + first - 1);
        f.accept(first);
        heap[last + first] = null;
        return result;
    }

    private void sink(int k) {
        doHeapify(k, (a, b) -> !unordered(a, b));
    }

    private void snake(@SuppressWarnings("SameParameterValue") int k) {
        swimUp(doHeapify(k, (a, b) -> !unordered(a, b)));
    }

    private void swimUp(int k) {
        int i = k;
        while (i > first && unordered(parent(i), i)) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    private int doHeapify(int k, BiPredicate<Integer, Integer> p) {
        int i = k;
        while (firstChild(i) <= last + first - 1) {
            int bestChild = firstChild(i);
            for (int j = 1; j < 4; j++) {
                int sibling = bestChild + j;
                if (sibling <= last + first - 1 && unordered(bestChild, sibling)) {
                    bestChild = sibling;
                }
            }
            if (p.test(i, bestChild)) break;
            swap(i, bestChild);
            i = bestChild;
        }
        return i;
    }

    private boolean unordered(int i, int j) {
        return (comparator.compare(heap[i], heap[j]) > 0) ^ max;
    }

    private void swap(int i, int j) {
        K tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    private int parent(int k) {
        return (k - first - 1) / 4 + first;
    }

    private int firstChild(int k) {
        return 4 * (k - first) + 1 + first;
    }

    private K peek(int k) {
        return heap[k];
    }

    private boolean getMax() {
        return max;
    }

    @Override
    public Iterator<K> iterator() {
        Collection<K> copy = new ArrayList<>(Arrays.asList(Arrays.copyOf(heap, last + first)));
        Iterator<K> result = copy.iterator();
        if (first > 0) result.next(); // strip off the leading null value.
        return result;
    }
}
