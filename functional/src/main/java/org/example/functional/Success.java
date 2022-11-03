package org.example.functional;

import java.util.function.Function;

final public class Success<T> extends Result<T> {

    private final T value;

    public Success(T value) {
        this.value = value;
    }

    @Override
    public <A> A fold(Function<T, A> success, Function<Exception, A> failure) {
        return success.apply(value);
    }

    @Override
    public T getOrThrow() {
        return value;
    }

    @Override
    public <A> Result<A> map(Function<T, A> f) {
        return new Success<>(f.apply(value));
    }

    @Override
    public <A> Result<A> flatMap(Function<T, Result<A>> f) {
        return f.apply(value);
    }
}
