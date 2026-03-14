package dev.creepysalhack.module;

import java.util.function.Consumer;

/**
 * A typed, observable module setting – equivalent to SalHack's Value<T>.
 * Supports Boolean, Integer, Double, Float, String, and Enum sub-types.
 */
public class Setting<T> {

    private final String name;
    private final String description;
    private T value;
    private final T min, max;
    private Consumer<T> onChange;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Setting(String name, String description, T defaultValue) {
        this(name, description, defaultValue, null, null);
    }

    public Setting(String name, String description, T defaultValue, T min, T max) {
        this.name        = name;
        this.description = description;
        this.value       = defaultValue;
        this.min         = min;
        this.max         = max;
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public T      getValue()       { return value; }
    public T      getMin()         { return min; }
    public T      getMax()         { return max; }

    public void setValue(T v) {
        if (min != null && max != null && v instanceof Comparable) {
            @SuppressWarnings("unchecked") Comparable<T> cv = (Comparable<T>) v;
            if (cv.compareTo(min) < 0) v = min;
            else if (cv.compareTo(max) > 0) v = max;
        }
        this.value = v;
        if (onChange != null) onChange.accept(v);
    }

    public Setting<T> onChanged(Consumer<T> c) { this.onChange = c; return this; }

    // ── Enum cycling ──────────────────────────────────────────────────────────

    public void cycleEnum(boolean reverse) {
        if (!(value instanceof Enum<?>)) return;
        Object[] constants = value.getClass().getEnumConstants();
        int idx = ((Enum<?>) value).ordinal();
        int next = reverse
                ? (idx == 0 ? constants.length - 1 : idx - 1)
                : (idx + 1) % constants.length;
        @SuppressWarnings("unchecked") T next2 = (T) constants[next];
        setValue(next2);
    }

    // ── Type helpers ──────────────────────────────────────────────────────────

    public boolean isBoolean() { return value instanceof Boolean; }
    public boolean isNumber()  { return value instanceof Number; }
    public boolean isEnum()    { return value instanceof Enum<?>; }
    public boolean isString()  { return value instanceof String; }

    @Override
    public String toString() { return name + "=" + value; }
}
