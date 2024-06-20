package org.example.hashingInDisk;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.example.ExtendHash;
import org.example.data.Rec;
import org.example.hashing.ExtendibleHashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExtendibleHashingInDisk implements ExtendHash { //implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(ExtendibleHashingInDisk.class);
    private static long serialVersionId = 1L;

    // size Bucket in bytes
    private static int maxBytesBucketSize;

    public List<Integer> listDirectories;
    //список buckets
    public List<BucketInDisk> buckets;
    public int globalDepth;

    // empty constructor without params for serialized by kryo
    public ExtendibleHashingInDisk() {

    }

    public ExtendibleHashingInDisk(int maxBytesBucketSize) {
        globalDepth = 1;
        this.maxBytesBucketSize = maxBytesBucketSize;
        //create directories and buckets
        listDirectories = new ArrayList<>((int) Math.pow(2, globalDepth));
        buckets = new ArrayList<>();
        buckets.add(new BucketInDisk(globalDepth, maxBytesBucketSize));
        buckets.add(new BucketInDisk(globalDepth, maxBytesBucketSize));
        listDirectories.add(0, 0);
        listDirectories.add(1, 1);
    }

    // storage class to disk in fileName
    public  void  saveToDisk(Kryo kryo, String fileName) throws IOException {
        saveToDisk(kryo, fileName, maxBytesBucketSize);
    }

    public  void  saveToDisk(Kryo kryo, String fileName, int bufferSize) throws IOException {
        /*BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileName), bufferSize);
        Output output = new Output(outputStream);
        kryo.writeObject(output, this);
        output.close();*/
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileName), bufferSize);
             Output output = new Output(outputStream)) {
            kryo.writeObject(output, this);
        }
    }

    // // load class from fileName using bufferSize = maxBytesBucketSize
    public  static ExtendibleHashingInDisk loadFromDisk(Kryo kryo, String fileName) throws IOException {
        return  loadFromDisk(kryo, fileName, maxBytesBucketSize);
    }

    // load class from fileName using bufferSize
    public  static ExtendibleHashingInDisk loadFromDisk(Kryo kryo, String fileName, int bufferSize) throws IOException {
        /*BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(fileName), bufferSize);
        Input input = new Input(inputStream);
        ExtendibleHashingInDisk extendibleHashingLoad = kryo.readObject(input, ExtendibleHashingInDisk.class);
        input.close();*/
        //ExtendibleHashingInDisk extendibleHashingLoad = new ExtendibleHashingInDisk();
        ExtendibleHashingInDisk extendibleHashingLoad;
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(fileName), bufferSize);
             Input input = new Input(inputStream)) {
            //Input input = new Input(inputStream);
            extendibleHashingLoad = kryo.readObject(input, ExtendibleHashingInDisk.class);
        }
        return extendibleHashingLoad;
    }

    public void insert(Rec data) {
        if (keyExists(data)) {
            // не разрешаем вставлять запись с ключом, который уже есть
            System.out.println("Key [" + data.getId() + "] is already present and cannot be added.");
            return;
        }
        String directoryIdBinary = getBinaryStr(data.getId(), globalDepth);
        int directoryKey = Integer.parseInt(directoryIdBinary, 2);
        //get bucket from directory
        int bucketIndex = listDirectories.get(directoryKey);
        BucketInDisk bucket = buckets.get(bucketIndex);
        if (bucket.insert(data) == -1) {
            splitBucket(directoryKey, data);
        }
    }

    /*private  String getBinaryStr(int num, int depth) {
        String binaryStr = Integer.toBinaryString(num);
        while (binaryStr.length() < depth) {
            binaryStr = "0" + binaryStr;
        }
        binaryStr = binaryStr.substring(binaryStr.length() - depth, binaryStr.length());
        return binaryStr;
    }*/

    private void splitBucket(int directoryKey, Rec lastKey) {
        Integer bucketIndex = listDirectories.get(directoryKey);
        BucketInDisk bucket = buckets.get(bucketIndex);
        if (bucket.getLocalDepth() == globalDepth) {
            expandDirectory();
        }
        if (bucket.getLocalDepth() < globalDepth) {
            // увеличиваем глубину существующего бакета на 1
            int newLocalDepth = bucket.getLocalDepth() + 1;
            bucket.setLocalDepth(newLocalDepth);
            // получаем все значения из бакета в новый List
            List<Rec> bucketKeys = new ArrayList<>(bucket.getKeys());
            // добавляем к значениям новое
            bucketKeys.add(lastKey);
            // очищаем значения в бакете
            bucket.clear();
            // создаем новый бакет
            BucketInDisk newBucket = new BucketInDisk(newLocalDepth, maxBytesBucketSize);
            buckets.add(newBucket);
            // запоминаем индекс нового бакета
            int newBucketIndex = buckets.size() - 1;
            //получаем все директории, которые ссылаются на этот бакет (хранятся индексы директорий)
            List <Integer> dirBuckets = new ArrayList<Integer>();
            for (int i = 0; i< listDirectories.size();i++) {
                // если директория ссылается на нужный бакет, то сохраняем ее в список
                if (listDirectories.get(i) == bucketIndex) {
                    dirBuckets.add(i);
                }
            }
            // бинарная строка для которой мы не меняем бакет
            String oldBinaryStr = getBinaryStr(directoryKey, newLocalDepth);
            // меняем бакеты для директорий, которые указывают на бакет для сплита
            for (int i = 0; i < dirBuckets.size(); i++) {
                String binStr = getBinaryStr(dirBuckets.get(i), newLocalDepth);
                if ( !binStr.equals(oldBinaryStr)) {
                    // Меняем бакет для директории на новый
                    listDirectories.set(dirBuckets.get(i), newBucketIndex);

                }
            }
            // раскидываем значения по бакетам
            for (Rec key : bucketKeys) {
                if (getBinaryStr(key.getId(), newLocalDepth).equals(oldBinaryStr)) {
                    if (bucket.insert(key) == -1) {
                        log.error("Bucket is full");
                    }
                }
                else {
                    if (newBucket.insert(key) == -1) {
                        log.error("Bucket is full");
                    }
                }
            }
        }
    }

    public void delete(Rec data) {
        String directoryIdBinary = getBinaryStr(data.getId(), globalDepth);
        int directoryKey = Integer.parseInt(directoryIdBinary, 2);
        //get bucket from directory
        int bucketIndex = listDirectories.get(directoryKey);
        BucketInDisk bucket = buckets.get(bucketIndex);

        if (bucket.getKeys().contains(data)) {
            bucket.getKeys().remove(data);
            System.out.println("Element " + data + " deleted successfully.");
        } else {
            System.out.println("Element " + data + " not found.");
        }
    }

    private void expandDirectory() {
        int currSize = listDirectories.size();
        for (int i = currSize; i < currSize * 2; i++){
            //новые директории указывают на те же бакеты, что указывали старые
            listDirectories.add(listDirectories.get(i - currSize));
        }
        globalDepth++;
    }

    public Boolean keyExists(Rec key) {
        int directoryId = Integer.parseInt(getBinaryStr(key.getId(), globalDepth), 2);
        int bucketIndex = listDirectories.get(directoryId);
        BucketInDisk bucket = buckets.get(bucketIndex);
        for (Rec item : bucket.getKeys()) {
            if (item.getId() == key.getId()) {
                return  true;
            }
        }
        return  false;
    }

    public Boolean search(Rec key) {
        int directoryId = Integer.parseInt(getBinaryStr(key.getId(), globalDepth), 2);
        int bucketIndex = listDirectories.get(directoryId);
        BucketInDisk bucket = buckets.get(bucketIndex);
        return bucket.getKeys().contains(key);
    }

    public  List<Integer> getlistDirectories() {
        return this.listDirectories;
    }
}


