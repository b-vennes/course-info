package org.example.functional;

import java.util.function.Function;
import java.util.function.Predicate;

public final class Empty<T> extends LazyList<T> {

    @Override
    public T head() {
        throw new IllegalArgumentException("Empty list has no head");
    }

    @Override
    public LazyList<T> tail() {
        throw new IllegalArgumentException("Empty list has no tail");
    }

    @Override
    public <A> LazyList<A> map(Function<T, A> f) {
        return new Empty<>();
    }

    @Override
    public LazyList<T> join(LazyList<T> other) {
        return other;
    }

    @Override
    public <A> A foldLeft(A initial, Function<A, Function<T, A>> f) {
        return initial;
    }

    @Override
    public LazyList<T> filter(Predicate<T> f) {
        return this;
    }

    @Override
    public int length() {
        return 0;
    }
}
