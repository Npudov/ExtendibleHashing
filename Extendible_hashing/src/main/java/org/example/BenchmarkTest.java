package org.example;

import org.example.hashing.ExtendibleHashing;
import org.example.data.Rec;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
@Warmup(iterations = 3 ,time = 300, timeUnit = TimeUnit.MILLISECONDS)
//@Measurement(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
public class BenchmarkTest {
    @Param({"10", "100", "1000"})
    private int N;

    private ExtendibleHashing extendibleHashing;

    private  String  tempStr = "tempValue";
    private final HashMap<Integer, Object> hashMap = new HashMap<>();

    public static void main(String[]args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(BenchmarkTest.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setUp() {
            extendibleHashing = new ExtendibleHashing();
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
}
