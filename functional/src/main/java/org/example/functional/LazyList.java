package org.example.functional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed abstract class LazyList<T> permits Empty, NonEmpty {
    public abstract T head();
    public abstract LazyList<T> tail();
    public abstract <A> LazyList<A> map(Function<T, A> f);
    public abstract LazyList<T> join(LazyList<T> other);

    public abstract <A> A foldLeft(A initial, Function<A, Function<T, A>> f);

    public abstract LazyList<T> filter(Predicate<T> f);

    public abstract int length();

    public LazyList<T> flatMap(Function<T, LazyList<T>> f) {
        return map(f).foldLeft(empty(), sum -> sum::join);
    }

    public static <T> LazyList<T> empty() {
        return new Empty<>();
    }

    public static <T> LazyList<T> make(Supplier<T> head, Supplier<LazyList<T>> tail) {
        return new NonEmpty<>(head, tail);
    }

    public static <T> LazyList<T> one(T value) {
        return make(() -> value, LazyList::empty);
    }

    public static <T> LazyList<T> list(List<T> values) {
        if (values.isEmpty()) {
            return empty();
        } else {
            return make(
                () -> values.get(0),
                () -> list(values.subList(1, values.size()))
            );
        }
    }

    public static <T> LazyList<T> resultSet(ResultSet resultSet, Function<ResultSet, Result<T>> decode) {
        boolean hasFirstElement;
        try {
            hasFirstElement = resultSet.next();
        } catch (SQLException e) {
            hasFirstElement = false;
        }

        if (hasFirstElement) {
            return decode.apply(resultSet)
                .fold(
                    value -> make(() -> value,
                    () -> resultSet(resultSet, decode)),
                    e -> empty()
                );
        } else {
            return empty();
        }
    }
}

