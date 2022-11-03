package org.example.functional;

import org.slf4j.Logger;

import java.util.function.Function;

public sealed abstract class Result<T> permits Failure, Success {
    public abstract <A> A fold(Function<T, A> success, Function<Exception, A> failure);
    public abstract T getOrThrow() throws Exception;
    public abstract <A> Result<A> map(Function<T, A> f);
    public abstract <A> Result<A> flatMap(Function<T, Result<A>> f);

    public final Result<T> recover(Function<Exception, T> f) {
        return fold(Result::pure, e -> pure(f.apply(e)));
    }

    public final Result<T> adaptError(Function<Exception, Exception> f) {
        return fold(Result::pure, e -> new Failure<>(f.apply(e)));
    }

    public final Result<T> onError(Function<Exception, Unit> f) {
        return adaptError(e -> {
            f.apply(e);
            return e;
        });
    }

    public final Result<T> tap(Function<T, Unit> f) {
        return map(t -> {
            f.apply(t);
            return t;
        });
    }

    public final Result<T> log(Function<T, String> toMessage, Logger logger) {
        return tap(t -> {
            logger.info(toMessage.apply(t));
            return Unit.get;
        });
    }

    public final Result<T> logError(String message, Logger logger) {
        return onError(e -> {
            logger.error(message, e);
            return Unit.get;
        });
    }

    public final Result<Unit> unit() {
        return map((T t) -> Unit.get);
    }

    public static <A> Result<A> success(A value) {
        return new Success<>(value);
    }

    public static <A> Result<A> failure(Exception error) {
        return new Failure<>(error);
    }

    public static <A> Result<A> pure(A value) {
        return success(value);
    }
}
