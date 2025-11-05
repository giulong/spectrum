package io.github.giulong.spectrum.pojos.testbook;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.AtomicDouble;

import io.github.giulong.spectrum.enums.Result;

import lombok.Getter;

@Getter
public class TestBookStatistics {

    private final AtomicInteger grandTotal = new AtomicInteger();
    private final AtomicInteger totalWeighted = new AtomicInteger();
    private final AtomicInteger grandTotalWeighted = new AtomicInteger();

    private final Map<Result, Statistics> totalCount = new HashMap<>();
    private final Map<Result, Statistics> grandTotalCount = new HashMap<>();
    private final Map<Result, Statistics> totalWeightedCount = new HashMap<>();
    private final Map<Result, Statistics> grandTotalWeightedCount = new HashMap<>();

    @Getter
    public static class Statistics {
        private final AtomicInteger total = new AtomicInteger();
        private final AtomicDouble percentage = new AtomicDouble();
    }
}
