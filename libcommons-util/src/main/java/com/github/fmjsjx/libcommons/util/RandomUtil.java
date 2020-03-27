package com.github.fmjsjx.libcommons.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RandomUtil {

    public interface Weighted {

        int weight();

    }

    private static final class DefaultRandomHolder {
        static final Random defaultRandom = new Random();
    }

    private static final Random defaultRandom() {
        return DefaultRandomHolder.defaultRandom;
    }

    private static final Random random(Random r) {
        return r == null ? defaultRandom() : r;
    }

    public static final int randomInRange(Random r, int min, int max) {
        if (max == min) {
            return min;
        }
        if (min > max) {
            throw new IllegalArgumentException("min(" + min + ") is greater than max(" + max + ")");
        }
        int bound = max + 1 - min;
        return random(r).nextInt(bound) + min;
    }

    public static final int randomInRange(int min, int max) {
        return randomInRange(null, min, max);
    }

    public static final int randomOne(int... values) {
        return randomOne(null, values);
    }

    public static final int randomOne(Random r, int... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        return values[random(r).nextInt(values.length)];
    }

    public static final long randomOne(long... values) {
        return randomOne(null, values);
    }

    public static final long randomOne(Random r, long... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("values.length must not be empty");
        }
        return values[random(r).nextInt(values.length)];
    }

    public static final <T> T randomOne(T[] values) {
        return randomOne(null, values);
    }

    public static final <T> T randomOne(Random r, T[] values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("values.length must not be empty");
        }
        return values[random(r).nextInt(values.length)];
    }

    public static final <E> E randomOne(List<E> values) {
        return randomOne(null, values);
    }

    public static final <E> E randomOne(Random r, List<E> values) {
        Objects.requireNonNull(values, "values must not be null");
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        return values.get(random(r).nextInt(values.size()));
    }

    public static final <E> E randomOne(Collection<E> values) {
        return randomOne(null, values);
    }

    @SuppressWarnings("unchecked")
    public static final <E> E randomOne(Random r, Collection<E> values) {
        if (values instanceof List) {
            return randomOne(r, (List<E>) values);
        }
        Objects.requireNonNull(values, "values must not be null");
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        Object[] array = values.toArray();
        return (E) array[random(r).nextInt(array.length)];
    }

    public static final int randomIndex(Random r, int... weights) {
        Objects.requireNonNull(weights, "weights must not be null");
        if (weights.length == 0) {
            throw new IllegalArgumentException("weights must not be empty");
        }
        int n = 0;
        for (int i : weights) {
            if (i < 0) {
                throw new IllegalArgumentException("all weights must >= 0: " + Arrays.toString(weights));
            }
            n += i;
            if (n < 0) {
                throw new IllegalArgumentException("too large weights: " + Arrays.toString(weights));
            }
        }
        n = random(r).nextInt(n);
        for (int i = 0; i < weights.length; i++) {
            n -= weights[i];
            if (n < 0) {
                return i;
            }
        }
        // can't reach this line
        return -1;
    }

    public static final int randomIndex(int... weights) {
        return randomIndex(null, weights);
    }

    @SuppressWarnings("unchecked")
    public static final <E extends Weighted> E randomOneWeighted(E... values) {
        return randomOneWeighted(null, values);
    }

    @SuppressWarnings("unchecked")
    public static final <E extends Weighted> E randomOneWeighted(Random r, E... values) {
        Objects.requireNonNull(values, "values must not be null");
        if (values.length == 0) {
            throw new IllegalArgumentException("values must not be empty");
        }
        int[] weights = new int[values.length];
        for (int i = 0; i < weights.length; i++) {
            weights[i] = values[i].weight();
        }
        return values[randomIndex(r, weights)];
    }

    public static final <E extends Weighted> E randomOneWeighted(List<E> values) {
        return randomOneWeighted(null, values);
    }

    public static final <E extends Weighted> E randomOneWeighted(Random r, List<E> values) {
        Objects.requireNonNull(values, "values must not be null");
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        int[] weights = values.stream().mapToInt(Weighted::weight).toArray();
        return values.get(randomIndex(r, weights));
    }

    public static final <E extends Weighted> E randomOneWeighted(Collection<E> values) {
        return randomOneWeighted(null, values);
    }

    @SuppressWarnings("unchecked")
    public static final <E extends Weighted> E randomOneWeighted(Random r, Collection<E> values) {
        if (values instanceof List) {
            return randomOneWeighted(r, (List<E>) values);
        }
        Objects.requireNonNull(values, "values must not be null");
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values must not be empty");
        }
        return randomOneWeighted(r, (E[]) values.toArray());
    }
    
    public static final long randomLong() {
        return randomLong(null);
    }
    
    public static final long randomLong(Random r) {
        return random(r).nextLong();
    }

}
