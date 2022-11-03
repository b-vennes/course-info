package org.example.courseinfo.cli;

import org.example.functional.*;

public record Configuration(String author) {
    public static Result<Configuration> parse(String[] args) throws IllegalArgumentException {
        return args.length > 0 ?
            Result.success(new Configuration(args[0]))
            : Result.failure(new IllegalArgumentException("No author provided."));
    }
}
