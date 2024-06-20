package org.example;

import com.esotericsoftware.kryo.Kryo;
import org.apache.log4j.BasicConfigurator;
import org.example.data.Rec;
import org.example.hashingInDisk.BucketInDisk;
import org.example.hashingInDisk.ExtendibleHashingInDisk;
import org.example.utils.Utils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.example.MainHashOnDisk.getRandomString;

@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 3 ,time = 300, timeUnit = TimeUnit.MILLISECONDS)
public class BenchmarkInDiskTest {
    @Param({"10", "100", "1000"})
    private int N;

    int bucketBSize = 2048;
    private ExtendibleHashingInDisk extendibleHashing;

    private String fileName = "extendibleHashingBenchmarkInDisk.ser";

    private static final Logger log = LoggerFactory.getLogger(BenchmarkInDiskTest.class);

    private  String  tempStr = "tempValue";
    private final HashMap<Integer, Object> hashMap = new HashMap<>();

    public static void main(String[]args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(BenchmarkInDiskTest.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setUp() {
        // гасим warn log4j
        org.apache.log4j.BasicConfigurator.configure();
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
        extendibleHashing = new ExtendibleHashingInDisk(bucketBSize);
        hashMap.put(1, "abc");
        hashMap.put(2, "cdf");
        hashMap.put(5, "klm");
        for (int i = 0; i < N; i++) {
            System.out.println("i = " + i);
            String endStr = "_" + (i + 1);
            extendibleHashing.insert(new Rec(i + 1, tempStr + endStr));
        }

    }

    @TearDown(Level.Iteration)
    public void teardown() {
        System.gc();
    }

    @Benchmark
    public void insertElement(Blackhole bh) {

        for (int i = 0; i < N; i++) {
            String endStr = "_" + (i + 1);
            extendibleHashing.insert(new Rec(i + 1, tempStr + endStr));
        }
        bh.consume(extendibleHashing);
    }

    @Benchmark
    public void searchElement(Blackhole bh) {
        for (int i = 0; i < N; i++) {
            String endStr = "_" + (i + 1);
            extendibleHashing.search(new Rec(i + 1, tempStr + endStr));
        }
        bh.consume(extendibleHashing);
    }

    @Benchmark
    public void deleteElement(Blackhole bh) {
        for (int i = 0; i < N; i++) {
            String endStr = "_" + (i + 1);
            extendibleHashing.delete(new Rec(i + 1, tempStr + endStr));
        }
        bh.consume(extendibleHashing);
    }

    @Benchmark
    public void serialize(Blackhole bh) throws IOException {
        for (int i = 0; i < N; i++) {

            // гасим warn log4j
            BasicConfigurator.configure();
            org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);

            Kryo kryo = new Kryo();
            kryo.register(ExtendibleHashingInDisk.class); //, new JavaSerializer());
            kryo.register(ArrayList.class);
            kryo.register(Rec.class);
            kryo.register(BucketInDisk.class);

            // Printing buckets
            Utils.printBucketsWithVal(extendibleHashing);

            // Сохраняем текущее состояние в файл
            log.info("Saving object ExtendibleHashing to file - " + fileName);
            extendibleHashing.saveToDisk(kryo, fileName);


            // Загружаем состояние из файла
            log.info("Loading object ExtendibleHashing from file - " + fileName);
            ExtendibleHashingInDisk extendibleHashingLoad = ExtendibleHashingInDisk.loadFromDisk(kryo, fileName);
            log.info("Load complete!");
            Utils.printBucketsWithVal(extendibleHashingLoad);
        }
    }
}
