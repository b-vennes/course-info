package org.example.functional;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class NonEmpty<T> extends LazyList<T> {
    private final Supplier<T> head;
    private final Supplier<LazyList<T>> tail;

    public NonEmpty(Supplier<T> head, Supplier<LazyList<T>> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public T head() {
        return head.get();
    }

    @Override
    public LazyList<T> tail() {
        return tail.get();
    }

    @Override
    public <A> LazyList<A> map(Function<T, A> f) {
        return new NonEmpty<>(() -> f.apply(head()), () -> tail().map(f));
    }

    @Override
    public LazyList<T> join(LazyList<T> other) {
        return new NonEmpty<>(head, () -> tail().join(other));
    }

    @Override
    public <A> A foldLeft(A initial, Function<A, Function<T, A>> f) {
        LazyList<T> current = this;
        A state = initial;

        while (current instanceof NonEmpty<T> nonEmpty) {
            state = f.apply(state).apply(nonEmpty.head());
            current = nonEmpty.tail();
        }

        return state;
    }

    @Override
    public LazyList<T> filter(Predicate<T> f) {
        if (f.test(head())) {
            return new NonEmpty<>(head, () -> tail().filter(f));
        } else {
            return tail().filter(f);
        }
    }

    @Override
    public int length() {
        return 1 + tail().length();
    }
}
