package com.itmo.list;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.SECONDS)
public class LinkedListBenchmark {

    private static final int SIZE = 1_000_000;

    @State(Scope.Thread)
    public static class ListState {
        private List<Integer> linkedList;
        private List<Integer> linkedList1K;
        private List<Integer> arrayList;

        private List<Integer> emptyLinkedList;
        private List<Integer> emptyArrayList;

        @Setup(Level.Invocation)
        public void setup() {
            linkedList = new LinkedList<>();
            linkedList1K = new LinkedList<>();
            arrayList = new ArrayList<>();

            for (int i = 0; i < SIZE; i++) {
                linkedList.add(i);
                arrayList.add(i);
            }

            for (int i = 0; i < 1000; i++) {
                linkedList1K.add(i);
            }

            emptyLinkedList = new LinkedList<>();
            emptyArrayList = new ArrayList<>();
        }

        @TearDown
        public void tearDown() {
            setup();
        }
    }

    @Benchmark
    public void iterateOver1MLinkedList(ListState state, Blackhole bh) {
        List<Integer> linkedList = state.linkedList;

        for (Integer i : linkedList) {
            bh.consume(i);
        }
    }

    @Benchmark
    public void iterateOver1MArrayList(ListState state, Blackhole bh) {
        List<Integer> arrayList = state.arrayList;

        for (Integer i : arrayList) {
            bh.consume(i);
        }
    }

    @Benchmark
    public void append1MLinkedList(ListState state, Blackhole bh) {
        List<Integer> linkedList = state.emptyLinkedList;

        for (int i = 0; i < SIZE; i++) {
            linkedList.add(i);
        }

        bh.consume(linkedList);
    }

    @Benchmark
    public void append1MArrayList(ListState state, Blackhole bh) {
        List<Integer> arrayList = state.emptyArrayList;

        for (int i = 0; i < SIZE; i++) {
            arrayList.add(i);
        }

        bh.consume(arrayList);
    }

    @Benchmark
    public void insert1KInLinkedListMiddle(ListState state, Blackhole bh) {
        List<Integer> linkedList = state.linkedList;

        for (int i = 0; i < 1000; i++) {
            int mid = linkedList.size() >>> 1;

            linkedList.add(mid, i);
        }

        bh.consume(linkedList);
    }

    @Benchmark
    public void insert1KInArrayListMiddle(ListState state, Blackhole bh) {
        List<Integer> arrayList = state.arrayList;

        for (int i = 0; i < 1000; i++) {
            int mid = arrayList.size() >>> 1;

            arrayList.add(mid, i);
        }

        bh.consume(arrayList);
    }

    @Benchmark
    public void iterateOver1KLinkedListByIndex(ListState state, Blackhole bh) {
        List<Integer> linkedList = state.linkedList1K;
        int size = linkedList.size();

        for (int i = 0; i < size; i++) {
            bh.consume(linkedList.get(i));
        }
    }

    @Benchmark
    public void iterateOver1MArrayListByIndex(ListState state, Blackhole bh) {
        List<Integer> arrayList = state.arrayList;

        for (int i = 0; i < SIZE; i++) {
            bh.consume(arrayList.get(i));
        }
    }

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(LinkedListBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(3)
                .measurementIterations(3)
                .jvmArgs("-XX:AutoBoxCacheMax=200002", "--enable-preview")
                .build();

        new Runner(opt).run();
    }
}
