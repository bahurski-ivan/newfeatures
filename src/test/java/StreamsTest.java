import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Ivan on 17/10/16.
 */
public class StreamsTest {

    @Test
    public void simpleTest() throws Exception {
        List<Integer> testSet = Arrays.asList(10, 15, 1, -1, 20, 300);

        Map<String, Integer> m = Streams.of(testSet)
                .filter(n -> n > 0)
                .filter(n -> n % 10 == 0)
                .transform(n -> new MyPair<String, Integer>(n.toString(), n * 2))
                .toMap(MyPair::getFirst, MyPair::getSecond);

        Assert.assertTrue(m.size() == 3);

        Assert.assertTrue(m.get("10") == 20);
        Assert.assertTrue(m.get("20") == 40);
        Assert.assertTrue(m.get("300") == 600);
    }

    @Test
    public void emptySetTest() throws Exception {
        List<Integer> testSet = Arrays.asList(10, 15, 1, -1, 20, 300);

        Map<String, Integer> m = Streams.of(testSet)
                .filter(n -> n > 1000)
                .toMap(n -> n.toString(), n -> n * 2);

        Assert.assertTrue(m.size() == 0);
    }

    private static class MyPair<T, U> {
        private T one;
        private U two;

        public MyPair(T one, U two) {
            this.one = one;
            this.two = two;
        }

        public T getFirst() {
            return one;
        }

        public U getSecond() {
            return two;
        }
    }


}