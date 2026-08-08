package com.adventofcode.utils;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class BlockingDequeWithMemory implements BlockingDeque<Integer> {
    private final BlockingDeque<Integer> deque;

    private int lastAddedElement;

    public int getLastAddedElement() {
        return lastAddedElement;
    }

    public BlockingDequeWithMemory(int size) {
        deque = new LinkedBlockingDeque<>(size);
    }

    @Override
    public void addFirst(@NonNull Integer element) {
        deque.addFirst(element);
        lastAddedElement = element;
    }

    @Override
    public void addLast(@NonNull Integer element) {
        deque.addLast(element);
        lastAddedElement = element;
    }

    @Override
    public boolean offerFirst(@NonNull Integer element) {
        lastAddedElement = element;
        return deque.offerFirst(element);
    }

    @Override
    public boolean offerLast(@NonNull Integer element) {
        lastAddedElement = element;
        return deque.offerLast(element);
    }

    @Override
    public void putFirst(@NonNull Integer element) throws InterruptedException {
        lastAddedElement = element;
        deque.putFirst(element);
    }

    @Override
    public void putLast(@NonNull Integer element) throws InterruptedException {
        lastAddedElement = element;
        deque.putLast(element);
    }

    @Override
    public boolean offerFirst(@NonNull Integer element, long timeout, TimeUnit unit) throws InterruptedException {
        lastAddedElement = element;
        return deque.offerFirst(element, timeout, unit);
    }

    @Override
    public boolean offerLast(@NonNull Integer element, long timeout, TimeUnit unit) throws InterruptedException {
        lastAddedElement = element;
        return deque.offerLast(element, timeout, unit);
    }

    @Override
    public @NonNull Integer takeFirst() throws InterruptedException {
        return deque.takeFirst();
    }

    @Override
    public @NonNull Integer takeLast() throws InterruptedException {
        return deque.takeLast();
    }

    @Override
    public Integer pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
        return deque.pollFirst(timeout, unit);
    }

    @Override
    public Integer pollLast(long timeout, TimeUnit unit) throws InterruptedException {
        return deque.pollLast(timeout, unit);
    }

    @Override
    public Integer removeFirst() {
        return deque.removeFirst();
    }

    @Override
    public Integer removeLast() {
        return deque.removeLast();
    }

    @Override
    public Integer pollFirst() {
        return deque.pollFirst();
    }

    @Override
    public Integer pollLast() {
        return deque.pollLast();
    }

    @Override
    public Integer getFirst() {
        return deque.getFirst();
    }

    @Override
    public Integer getLast() {
        return deque.getLast();
    }

    @Override
    public Integer peekFirst() {
        return deque.peekFirst();
    }

    @Override
    public Integer peekLast() {
        return deque.peekLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object element) {
        return deque.removeFirstOccurrence(element);
    }

    @Override
    public boolean removeLastOccurrence(Object element) {
        return deque.removeLastOccurrence(element);
    }

    @Override
    public boolean add(@NonNull Integer element) {
        lastAddedElement = element;
        return deque.add(element);
    }

    @Override
    public boolean offer(@NonNull Integer element) {
        lastAddedElement = element;
        return deque.offer(element);
    }

    @Override
    public void put(@NonNull Integer element) throws InterruptedException {
        lastAddedElement = element;
        deque.put(element);
    }

    @Override
    public boolean offer(@NonNull Integer element, long timeout, @NonNull TimeUnit unit) throws InterruptedException {
        lastAddedElement = element;
        return deque.offer(element, timeout, unit);
    }

    @Override
    public @NonNull Integer remove() {
        return deque.remove();
    }

    @Override
    public Integer poll() {
        return deque.poll();
    }

    @Override
    public @NonNull Integer take() throws InterruptedException {
        return deque.take();
    }

    @Override
    public Integer poll(long timeout, TimeUnit unit) throws InterruptedException {
        return deque.poll(timeout, unit);
    }

    @Override
    public @NonNull Integer element() {
        return deque.element();
    }

    @Override
    public Integer peek() {
        return deque.peek();
    }

    @Override
    public int remainingCapacity() {
        return deque.remainingCapacity();
    }

    @Override
    public int drainTo(@NonNull Collection<? super Integer> collection) {
        return deque.drainTo(collection);
    }

    @Override
    public int drainTo(@NonNull Collection<? super Integer> collection, int maxElements) {
        return deque.drainTo(collection, maxElements);
    }

    @Override
    public void push(@NonNull Integer element) {
        lastAddedElement = element;
        deque.push(element);
    }

    @Override
    public Integer pop() {
        return deque.pop();
    }

    @Override
    public boolean remove(Object element) {
        return deque.remove(element);
    }

    @Override
    public boolean contains(Object element) {
        return deque.contains(element);
    }

    @Override
    public int size() {
        return deque.size();
    }

    @Override
    public boolean isEmpty() {
        return deque.isEmpty();
    }

    @Override
    public @NonNull Iterator<Integer> iterator() {
        return deque.iterator();
    }

    @Override
    public @NonNull Iterator<Integer> descendingIterator() {
        return deque.descendingIterator();
    }

    @Override
    public Object @NonNull [] toArray() {
        return deque.toArray();
    }

    @Override
    public <T> T @NonNull [] toArray(T @NonNull [] array) {
        return deque.toArray(array);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return deque.containsAll(collection);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Integer> collection) {
        throw new UnsupportedOperationException("addAll");
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        return deque.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return deque.retainAll(collection);
    }

    @Override
    public void clear() {
        deque.clear();
    }
}
