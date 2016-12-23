import java.util.*;

public class CuckooMap<K, V> implements Map<K, V> {
    private int size;
    private int startMassLength;

    private Entry<K, V>[][] mass = new Entry[2][];

    private int hash1(Object key) {
        int hash = (key.hashCode() * 1291) % mass[0].length;
        if (hash < 0) {
            hash += mass[0].length;
        }
        return hash;
    }
    private int hash2(Object key) {
        int hash = (key.hashCode() * 1291) % mass[1].length;
        if (hash < 0) {
            hash += mass[1].length;
        }
        return hash;
    }
    private int hash(int hash, Object key) {
        if (hash == 0) {
            return hash1(key);
        } else {
            return hash2(key);
        }
    }

    public CuckooMap() {
        this(10000);
    }

    public CuckooMap(int s) {
        startMassLength = s;
        init();
    }

    private void init() {
        size = 0;
        mass[0] = new Entry[startMassLength];
        mass[1] = new Entry[startMassLength];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private Entry<Integer, Integer> findPos(Object key) {
        for (int i = 0; i < 2; i++) {
            if (mass[i][hash(i, key)] != null && mass[i][hash(i, key)].getKey().equals(key)) {
                return new AbstractMap.SimpleEntry<>(i, hash(i, key));
            }
        }

        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        return findPos(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Entry e : mass[0]) {
            if (e != null && e.getValue().equals(value)) {
                return true;
            }
        }
        for (Entry e : mass[1]) {
            if (e != null && e.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        Entry<Integer, Integer> found = findPos(key);
        if (found != null) {
            return (V) mass[found.getKey()][found.getValue()].getValue();
        }
        return null;
    }

    private void updateMass() {
        Entry<K, V>[] all = (Entry<K, V>[]) entrySet().toArray();
        size = 0;
        mass[0] = new Entry[mass[0].length * 2 + 1];
        mass[1] = new Entry[mass[1].length * 2 + 1];
        for (Entry<K, V> e : all) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public V put(K key, V value) {
        Entry<Integer, Integer> found = findPos(key);
        if (found != null) {
            mass[found.getKey()][found.getValue()].setValue(value);
            return value;
        }

        Entry<K, V> startingElem = new AbstractMap.SimpleEntry<>(key, value);
        size++;
        int hashMass = 0;
        Entry<K, V> e = startingElem, lastE;
        do {
            lastE = mass[hashMass][hash(hashMass, e.getKey())];
            mass[hashMass][hash(hashMass, e.getKey())] = e;
            e = lastE;
            if (e == startingElem) {
                updateMass();
                put(key, value);
                break;
            }

            if (hashMass == 0) {
                hashMass = 1;
            } else {
                hashMass = 0;
            }
        } while (e != null);

        return value;
    }

    @Override
    public V remove(Object key) {
        Entry<Integer, Integer> found = findPos(key);

        if (found == null) {
            return null;
        }

        V value = (V) mass[found.getKey()][found.getValue()].getValue();
        mass[found.getKey()][found.getValue()] = null;
        size--;

        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Entry<K, V>[] s = (Entry<K, V>[]) m.entrySet().toArray();
        for (Entry<K, V> e : s) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < mass[i].length; j++) {
                mass[i][j] = null;
            }
        }
        mass[0] = null;
        mass[1] = null;
        init();
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < mass[i].length; j++) {
                if (mass[i][j] != null) {
                    set.add(mass[i][j].getKey());
                }
            }
        }

        return set;
    }

    @Override
    public Collection<V> values() {
        List<V> list = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < mass[i].length; j++) {
                if (mass[i][j] != null) {
                    list.add(mass[i][j].getValue());
                }
            }
        }

        return list;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> set = new HashSet<>();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < mass[i].length; j++) {
                if (mass[i][j] != null) {
                    set.add(mass[i][j]);
                }
            }
        }

        return set;
    }
}