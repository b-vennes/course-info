package org.example.courseinfo.types;

import java.util.stream.Collector;

public final class Unit {
    public static final Unit get = new Unit();
    public static final Collector<Unit, Unit, Unit> collector = Collector.of(
        () -> Unit.get,
        (unit, unit2) -> {},
        (unit, unit2) -> Unit.get
    );
}
