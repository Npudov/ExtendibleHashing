package org.example.utils;

import org.example.hashing.Bucket;
import org.example.hashing.ExtendibleHashing;
import org.example.data.Rec;
import org.example.hashingInDisk.BucketInDisk;
import org.example.hashingInDisk.ExtendibleHashingInDisk;

public class Utils {
    public static void printBucketsWithVal() {
        for (int i = 0; i< ExtendibleHashing.listDirectories.size(); i++) {
            int bucketIndex = ExtendibleHashing.listDirectories.get(i);
            Bucket bucket = ExtendibleHashing.buckets.get(bucketIndex);
            System.out.print("Directory " + i + ": ");
            for (Rec key : bucket.getKeys()) {
                System.out.print(key.getId() + " (" + key.getValue() + ") ");
            }
            System.out.println();
        }
    }

    public static void printBucketsWithVal(ExtendibleHashingInDisk extendibleHashing) {
        for (int i = 0; i<extendibleHashing.getlistDirectories().size(); i++) {
            int bucketIndex = extendibleHashing.getlistDirectories().get(i);
            BucketInDisk bucket = extendibleHashing.buckets.get(bucketIndex);
            System.out.print("Directory " + i + ": ");
            for (Rec key : bucket.getKeys()) {
                System.out.print(key.getId() + " (" + key.getValue() + ") ");
            }
            System.out.println();
        }
    }
}

