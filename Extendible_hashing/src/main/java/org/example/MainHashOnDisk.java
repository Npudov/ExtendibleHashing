package org.example;

import com.esotericsoftware.kryo.Kryo;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.example.data.Rec;
import org.example.hashingInDisk.BucketInDisk;
import org.example.hashingInDisk.ExtendibleHashingInDisk;
import org.example.utils.Utils;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainHashOnDisk {
    private static final Logger log = LoggerFactory.getLogger(MainHashOnDisk.class);

    public  static  String getRandomString(int len) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = len;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)) //only english letters
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return  generatedString;
    }

    public static void main(String[] args) throws IOException {
        String fileName = "extendibleHashingInDisk.ser"; // Имя файла для сохранения/загрузки состояния
        int bucketBSize = 256;

        // гасим warn log4j
        BasicConfigurator.configure();
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);

        Kryo kryo = new Kryo();
        kryo.register(ExtendibleHashingInDisk.class); //, new JavaSerializer());
        kryo.register(ArrayList.class);
        kryo.register(Rec.class);
        kryo.register(BucketInDisk.class);

        // Создаем новый объект ExtendibleHashing
        ExtendibleHashingInDisk extendibleHashingInDisk = new ExtendibleHashingInDisk(bucketBSize);

        // generate data
        for (int i = 0;  i < 100; i++) {
            String str = getRandomString(10);
            extendibleHashingInDisk.insert(new Rec(i, str + "_" + i));
        }

        // Printing buckets
        Utils.printBucketsWithVal(extendibleHashingInDisk);

        // Сохраняем текущее состояние в файл
        log.info("Saving object ExtendibleHashing to file - " + fileName);
        extendibleHashingInDisk.saveToDisk(kryo, fileName);

        // Загружаем состояние из файла
        log.info("Loading object ExtendibleHashing from file - " + fileName);
        ExtendibleHashingInDisk extendibleHashingLoad = ExtendibleHashingInDisk.loadFromDisk(kryo, fileName);
        log.info("Load complete!");
        Utils.printBucketsWithVal(extendibleHashingLoad);
    }
}
