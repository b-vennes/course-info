package org.example.courseinfo.cli;

import reactor.core.publisher.Mono;

public record Configuration(String author) {
    public static Mono<Configuration> parse(String[] args) throws IllegalArgumentException {
        return args.length > 0 ?
            Mono.just(new Configuration(args[0])) :
            Mono.error(new IllegalArgumentException("No author provided."));
    }
}
