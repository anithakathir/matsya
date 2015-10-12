package matsya.store;

import matsya.tsdb.Key;
import matsya.tsdb.KeyNotFoundException;
import matsya.tsdb.TSDB;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * InMemory implementation of TimeSeries Database for testing purposes.
 */
public class InMemoryTSDB extends TSDB {
    Map<Key, ByteBuffer> points = new TreeMap<>();

    @Override
    public void put(long timestamp, byte[] key, byte[] value) throws IOException {
        points.put(Key.of(key, timestamp), ByteBuffer.wrap(value));
    }

    @Override
    public byte[] get(long timestamp, byte[] key) throws IOException, KeyNotFoundException {
        ByteBuffer value = points.get(Key.of(key, timestamp));
        if (value == null)
            throw new KeyNotFoundException("timestamp=" + timestamp + ", bytes=" + Arrays.toString(key) + " is not found.");

        return value.array();
    }

    @Override
    public byte[][] scan(long start, long end, byte[] keyPrefix) throws Exception {
        ArrayList<byte[]> values = new ArrayList<>();
        for (Key key : points.keySet()) {
            if (key.isAfter(start) && key.isBefore(end) && key.prefixMatch(keyPrefix))
                values.add(points.get(key).array());
        }
        return values.toArray(new byte[0][]);
    }
}
