import org.example.hashing.ExtendibleHashing;
import org.example.data.Rec;
import org.example.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtendibleHashingTest {
    private ExtendibleHashing extendibleHashing;


    private HashMap<Integer, Object> hashMap = new HashMap<>();

    @BeforeEach
    public void setUp() {
        extendibleHashing = new ExtendibleHashing();
        hashMap.put(1, "abc");
        hashMap.put(2, "cdf");
        hashMap.put(5, "klm");
    }

    @Test
    public void testInsert() {
        extendibleHashing.insert(new Rec(1, hashMap));
        extendibleHashing.insert(new Rec(2, hashMap));
        extendibleHashing.insert(new Rec(4, hashMap));

        extendibleHashing.insert(new Rec(6, hashMap));
        extendibleHashing.insert(new Rec(8, hashMap));
        extendibleHashing.insert(new Rec(3, hashMap));

        extendibleHashing.insert(new Rec(5, hashMap));
        extendibleHashing.insert(new Rec(9, hashMap));
        extendibleHashing.insert(new Rec(15, hashMap));

        Utils.printBucketsWithVal();

        assertTrue(ExtendibleHashing.buckets.get(1).getKeys().contains(new Rec(1, hashMap)));
        assertTrue(ExtendibleHashing.buckets.get(2).getKeys().contains(new Rec(6, hashMap)));

        assertFalse(ExtendibleHashing.buckets.get(3).getKeys().contains(new Rec(2, hashMap)));
        assertFalse(ExtendibleHashing.buckets.get(0).getKeys().contains(new Rec(5, hashMap)));
    }

    @Test
    public void testDelete() {
        extendibleHashing.insert(new Rec(1, hashMap));
        extendibleHashing.insert(new Rec(2, hashMap));
        extendibleHashing.insert(new Rec(4, hashMap));

        Utils.printBucketsWithVal();

        extendibleHashing.delete(new Rec(2, hashMap));

        Utils.printBucketsWithVal();

        assertFalse(ExtendibleHashing.buckets.get(0).getKeys().contains(new Rec(2, hashMap)));
        assertTrue(ExtendibleHashing.buckets.get(1).getKeys().contains(new Rec(1, hashMap)));
    }

    @Test
    public void testSearch() {
        extendibleHashing.insert(new Rec(1, hashMap));
        extendibleHashing.insert(new Rec(2, hashMap));
        extendibleHashing.insert(new Rec(4, hashMap));

        extendibleHashing.insert(new Rec(6, hashMap));
        extendibleHashing.insert(new Rec(8, hashMap));
        extendibleHashing.insert(new Rec(3, hashMap));

        extendibleHashing.insert(new Rec(5, hashMap));
        extendibleHashing.insert(new Rec(9, hashMap));
        extendibleHashing.insert(new Rec(15, hashMap));

        Utils.printBucketsWithVal();

        assertTrue(extendibleHashing.search(new Rec(1, hashMap)));
        assertTrue(extendibleHashing.search(new Rec(2, hashMap)));

        assertFalse(extendibleHashing.search(new Rec(25, hashMap)));
        assertFalse(extendibleHashing.search(new Rec(13, hashMap)));


    }
}