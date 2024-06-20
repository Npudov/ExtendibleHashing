package org.example.hashing;

import org.example.ExtendHash;
import org.example.data.Rec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExtendibleHashing implements ExtendHash, Serializable {

    private static long serialVersionId = 1L;

    private static final int MAX_BUCKET_SIZE = 3;

    public static List<Integer> listDirectories;
    //список buckets
    public static List<Bucket> buckets;
    public static int globalDepth;


    public ExtendibleHashing() {
        globalDepth = 1;
        //create directories and buckets
        listDirectories = new ArrayList<>((int) Math.pow(2, globalDepth));
        buckets = new ArrayList<>();
        buckets.add(new Bucket(globalDepth, MAX_BUCKET_SIZE));
        buckets.add(new Bucket(globalDepth, MAX_BUCKET_SIZE));
        listDirectories.add(0, 0);
        listDirectories.add(1, 1);
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
        Bucket bucket = buckets.get(bucketIndex);
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
        Bucket bucket = buckets.get(bucketIndex);
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
            Bucket newBucket = new Bucket(newLocalDepth, MAX_BUCKET_SIZE);
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
                    bucket.insert(key);
                }
                else {
                    newBucket.insert(key);
                }
            }
        }
    }

    public void delete(Rec data) {
        String directoryIdBinary = getBinaryStr(data.getId(), globalDepth);
        int directoryKey = Integer.parseInt(directoryIdBinary, 2);
        //get bucket from directory
        int bucketIndex = listDirectories.get(directoryKey);
        Bucket bucket = buckets.get(bucketIndex);

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
        Bucket bucket = buckets.get(bucketIndex);
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
        Bucket bucket = buckets.get(bucketIndex);
        return bucket.getKeys().contains(key);
    }

    public void serialize(String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(Paths.get(fileName)))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ExtendibleHashing deserialize(String fileName) {
        ExtendibleHashing extendibleHashing = null;
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(Paths.get(fileName)))) {
            extendibleHashing = (ExtendibleHashing) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return extendibleHashing;
    }
}

