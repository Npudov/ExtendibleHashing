import org.example.hashing.Bucket;
import org.example.data.Rec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class BucketTest {
    private Bucket bucket;

    private static final int MAX_BUCKET_SIZE = 3;

    private HashMap<Integer, Object> hashMap = new HashMap<>();

    @BeforeEach
    public void setUp() {
        bucket = new Bucket(1, MAX_BUCKET_SIZE);
        hashMap.put(1, "abc");
        hashMap.put(2, "cdf");
        hashMap.put(5, "klm");
    }

    @Test
    public void testInsert() {
        Rec rec_1 = new Rec(1, hashMap);
        Rec rec_2 = new Rec(2, hashMap);
        Rec rec_3 = new Rec(3, hashMap);
        bucket.insert(rec_1);
        bucket.insert(rec_2);
        bucket.insert(rec_3);
        assertEquals(3, bucket.getKeys().size());
        assertEquals(1, bucket.getKeys().get(0).getId());
    }

    @Test
    public void testIsFull() {
        Rec rec_1 = new Rec(1, hashMap);
        Rec rec_2 = new Rec(2, hashMap);
        Rec rec_3 = new Rec(3, hashMap);
        assertFalse(bucket.isFull());
        bucket.insert(rec_1);
        bucket.insert(rec_2);
        bucket.insert(rec_3);
        assertTrue(bucket.isFull());
    }

    @Test
    public void testClear() {
        Rec rec_1 = new Rec(1, hashMap);
        Rec rec_2 = new Rec(2, hashMap);
        Rec rec_3 = new Rec(3, hashMap);
        bucket.insert(rec_1);
        bucket.insert(rec_2);
        bucket.insert(rec_3);
        bucket.clear();
        assertEquals(0, bucket.getKeys().size());
    }
}
