package com.adventofcode.utils;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class BlockingDequeWithMemory implements BlockingDeque<Long> {
    private final BlockingDeque<Long> deque;

    private long lastAddedElement;

    public long getLastAddedElement() {
        return lastAddedElement;
    }

    public BlockingDequeWithMemory(int size) {
        deque = new LinkedBlockingDeque<>(size);
    }

    @Override
    public void addFirst(@NonNull Long element) {
        deque.addFirst(element);
        lastAddedElement = element;
    }

    @Override
    public void addLast(@NonNull Long element) {
        deque.addLast(element);
        lastAddedElement = element;
    }

    @Override
    public boolean offerFirst(@NonNull Long element) {
        lastAddedElement = element;
        return deque.offerFirst(element);
    }

    @Override
    public boolean offerLast(@NonNull Long element) {
        lastAddedElement = element;
        return deque.offerLast(element);
    }

    @Override
    public void putFirst(@NonNull Long element) throws InterruptedException {
        lastAddedElement = element;
        deque.putFirst(element);
    }

    @Override
    public void putLast(@NonNull Long element) throws InterruptedException {
        lastAddedElement = element;
        deque.putLast(element);
    }

    @Override
    public boolean offerFirst(@NonNull Long element, long timeout, TimeUnit unit) throws InterruptedException {
        lastAddedElement = element;
        return deque.offerFirst(element, timeout, unit);
    }

    @Override
    public boolean offerLast(@NonNull Long element, long timeout, TimeUnit unit) throws InterruptedException {
        lastAddedElement = element;
        return deque.offerLast(element, timeout, unit);
    }

    @Override
    public @NonNull Long takeFirst() throws InterruptedException {
        return deque.takeFirst();
    }

    @Override
    public @NonNull Long takeLast() throws InterruptedException {
        return deque.takeLast();
    }

    @Override
    public Long pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
        return deque.pollFirst(timeout, unit);
    }

    @Override
    public Long pollLast(long timeout, TimeUnit unit) throws InterruptedException {
        return deque.pollLast(timeout, unit);
    }

    @Override
    public Long removeFirst() {
        return deque.removeFirst();
    }

    @Override
    public Long removeLast() {
        return deque.removeLast();
    }

    @Override
    public Long pollFirst() {
        return deque.pollFirst();
    }

    @Override
    public Long pollLast() {
        return deque.pollLast();
    }

    @Override
    public Long getFirst() {
        return deque.getFirst();
    }

    @Override
    public Long getLast() {
        return deque.getLast();
    }

    @Override
    public Long peekFirst() {
        return deque.peekFirst();
    }

    @Override
    public Long peekLast() {
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
    public boolean add(@NonNull Long element) {
        lastAddedElement = element;
        return deque.add(element);
    }

    @Override
    public boolean offer(@NonNull Long element) {
        lastAddedElement = element;
        return deque.offer(element);
    }

    @Override
    public void put(@NonNull Long element) throws InterruptedException {
        lastAddedElement = element;
        deque.put(element);
    }

    @Override
    public boolean offer(@NonNull Long element, long timeout, @NonNull TimeUnit unit) throws InterruptedException {
        lastAddedElement = element;
        return deque.offer(element, timeout, unit);
    }

    @Override
    public @NonNull Long remove() {
        return deque.remove();
    }

    @Override
    public Long poll() {
        return deque.poll();
    }

    @Override
    public @NonNull Long take() throws InterruptedException {
        return deque.take();
    }

    @Override
    public Long poll(long timeout, TimeUnit unit) throws InterruptedException {
        return deque.poll(timeout, unit);
    }

    @Override
    public @NonNull Long element() {
        return deque.element();
    }

    @Override
    public Long peek() {
        return deque.peek();
    }

    @Override
    public int remainingCapacity() {
        return deque.remainingCapacity();
    }

    @Override
    public int drainTo(@NonNull Collection<? super Long> collection) {
        return deque.drainTo(collection);
    }

    @Override
    public int drainTo(@NonNull Collection<? super Long> collection, int maxElements) {
        return deque.drainTo(collection, maxElements);
    }

    @Override
    public void push(@NonNull Long element) {
        lastAddedElement = element;
        deque.push(element);
    }

    @Override
    public Long pop() {
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
    public @NonNull Iterator<Long> iterator() {
        return deque.iterator();
    }

    @Override
    public @NonNull Iterator<Long> descendingIterator() {
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
    public boolean addAll(@NonNull Collection<? extends Long> collection) {
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
