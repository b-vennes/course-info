package org.example.functional;

import java.util.function.Function;

final public class Failure<T> extends Result<T> {

    private final Exception exception;

    public Failure(Exception exception) {
        this.exception = exception;
    }

    @Override
    public <A> A fold(Function<T, A> success, Function<Exception, A> failure) {
        return failure.apply(exception);
    }

    @Override
    public T getOrThrow() throws Exception {
        throw exception;
    }

    @Override
    public <A> Result<A> map(Function<T, A> f) {
        return new Failure<>(exception);
    }

    @Override
    public <A> Result<A> flatMap(Function<T, Result<A>> f) {
        return new Failure<>(exception);
    }
}
