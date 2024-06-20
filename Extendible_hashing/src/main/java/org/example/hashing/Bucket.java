package org.example.hashing;

import org.example.data.Rec;

import java.util.ArrayList;
import java.util.List;


public class Bucket {
    private int localDepth;

    private List<Rec> keys;


    private final int MAX_BUCKET_SIZE;

    public Bucket(int localDepth, int bucket_size) {
        this.localDepth = localDepth;
        this.keys = new ArrayList<>();
        this.MAX_BUCKET_SIZE = bucket_size;
    }

    public int getLocalDepth() {
        return localDepth;
    }

    public void setLocalDepth(int localDepth) {
        this.localDepth = localDepth;
    }

    public List<Rec> getKeys() {
        return keys;
    }

    public boolean isFull() {
        return keys.size() >= MAX_BUCKET_SIZE;
    }

    public int insert(Rec key) {
        if (!isFull()) {
            keys.add(key);
            return keys.size() - 1;
        } else {
            return -1;
        }
    }

    // clear all values
    public void clear() {
        keys.clear();
    }
}
