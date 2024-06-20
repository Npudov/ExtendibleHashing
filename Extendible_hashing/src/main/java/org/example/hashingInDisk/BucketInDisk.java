package org.example.hashingInDisk;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.example.data.Rec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class BucketInDisk { //implements KryoSerializable {
    private static final Logger log = LoggerFactory.getLogger(BucketInDisk.class);
    private int localDepth;
    private List<Rec> keys;
    private final int maxBytesBucketSize;

    private long sizeInBytes;

    // constructor without params for serialized by kryo
    public BucketInDisk() {
        maxBytesBucketSize = 0;
    }

    public BucketInDisk(int localDepth, int bucket_size) {
        this.localDepth = localDepth;
        this.keys = new ArrayList<>();
        this.maxBytesBucketSize = bucket_size;
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


    public int insert(Rec key) {
        // get size of keys
        long busyBytes = sizeInBytes;
        long sizeKey = getSizeInBytes(key);
        if (busyBytes + sizeKey <= maxBytesBucketSize) {
            keys.add(key);
            sizeInBytes = sizeInBytes + sizeKey;
            return keys.size() - 1;
        } else {
            return -1;
        }
    }

    // clear all values
    public void clear() {
        sizeInBytes = 0;
        keys.clear();
    }

    // calc size object
    public  long getSizeInBytes(Object obj) {
        long res = 0;
        // serialize obj and get size bytes
        Kryo kryo = new Kryo();
        kryo.register(Rec.class);
        kryo.register(ArrayList.class);
        kryo.setRegistrationRequired(false);
        try (Output output = new Output(new ByteArrayOutputStream())) {
            kryo.writeObject(output, obj);
            log.debug("total = " + output.total());
            res = output.total();
        }
        return  res;
    }
}
