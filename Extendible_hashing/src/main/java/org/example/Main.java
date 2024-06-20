package org.example;

import org.example.hashing.ExtendibleHashing;
import org.example.data.Rec;
import org.example.utils.Utils;

public class Main {
    public static void main(String[] args) {
        String fileName = "extendibleHashing.ser"; // Имя файла для сохранения/загрузки состояния

        // Создаем новый объект ExtendibleHashing
        ExtendibleHashing extendibleHashing = new ExtendibleHashing();

        // Inserting values
        extendibleHashing.insert(new Rec(1, "abc"));
        extendibleHashing.insert(new Rec(2, "cdf"));
        extendibleHashing.insert(new Rec(3, "klm"));

        // Printing buckets
        Utils.printBucketsWithVal();

        // Сохраняем текущее состояние в файл
        extendibleHashing.serialize(fileName);

        // Загружаем состояние из файла
        extendibleHashing = ExtendibleHashing.deserialize(fileName);

        // Printing buckets
        Utils.printBucketsWithVal();
    }
}