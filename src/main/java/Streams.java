import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Ivan on 16/10/16.
 */
public class Streams<T> {

    private Iterator<? extends T> valueProducer;

    private Streams(Iterator<? extends T> target) {
        this.valueProducer = target;
    }

    public static <T> Streams<T> of(List<? extends T> list) {
        return new Streams<T>(list.iterator());
    }

    @SuppressWarnings("unchecked")
    public Streams<T> filter(Predicate<? super T> predicate) {
        if (!(valueProducer instanceof PredicateIterator))
            valueProducer = new PredicateIterator<T>(valueProducer);
        PredicateIterator.class.cast(valueProducer).appendFilter(predicate);
        return this;
    }

    public <R> Streams<R> transform(Function<? super T, ? extends R> mapper) {
        return new Streams<R>(new Iterator<R>() {

            @Override
            public boolean hasNext() {
                return valueProducer.hasNext();
            }

            @Override
            public R next() {
                return mapper.apply(valueProducer.next());
            }
        });
    }

    public <K, V> Map<K, V> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        Map<K, V> result = new HashMap<K, V>();

        while (valueProducer.hasNext()) {
            T value = valueProducer.next();
            result.put(keyMapper.apply(value), valueMapper.apply(value));
        }

        return result;
    }

    private static class PredicateIterator<T> implements Iterator<T> {
        private List<Predicate<? super T>> filters = new ArrayList<>();
        private Iterator<? extends T> dataSource;

        private T nextValue = null;

        public PredicateIterator(Iterator<? extends T> src) {
            dataSource = src;
        }

        public void appendFilter(Predicate<? super T> filter) {
            filters.add(filter);
        }

        @Override
        public boolean hasNext() {
            while (nextValue == null && dataSource.hasNext()) {
                T value = dataSource.next();
                if (check(value))
                    nextValue = value;
            }
            return nextValue != null;
        }

        @Override
        public T next() {
            if (nextValue == null && !hasNext())
                throw new NoSuchElementException();
            T result = nextValue;
            nextValue = null;
            return result;
        }

        private boolean check(T value) {
            for (Predicate<? super T> p : filters)
                if (!p.test(value))
                    return false;
            return true;
        }
    }
}
