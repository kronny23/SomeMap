import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class CuckooMapTests {
    @Test public void test() {
        Map<String, Integer> map = new CuckooMap<>();

        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("two", 4);

        assertTrue(map.get("two").equals(4));
        assertTrue(map.get("four") == null);
        assertTrue(map.containsKey("three"));
        assertTrue(!map.containsKey("four"));
        assertTrue(map.size() == 3);
        assertTrue(!map.isEmpty());
        assertTrue(map.containsValue(4));
        assertTrue(!map.containsValue(2));

        map.remove("one");

        assertTrue(map.containsKey("three"));
        assertTrue(!map.containsKey("one"));
        assertTrue(map.size() == 2);
        assertTrue(!map.isEmpty());
        assertTrue(map.containsValue(4));
        assertTrue(!map.containsValue(1));

        map.remove("two");
        map.remove("three");

        assertTrue(map.isEmpty());

        map.put("four", 4);
        map.put("one", 1);
        map.clear();

        assertTrue(map.isEmpty());
    }
}
