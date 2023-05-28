package com.giuliolongfils.spectrum.pojos.testbook;

import com.giuliolongfils.spectrum.pojos.testbook.TestBookResult.Status;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class TestBookStatistics {

    private final AtomicInteger grandTotal = new AtomicInteger();
    private final AtomicInteger totalWeighted = new AtomicInteger();
    private final AtomicInteger grandTotalWeighted = new AtomicInteger();

    private final Map<Status, TestStatistics> totalCount = new HashMap<>();
    private final Map<Status, TestStatistics> grandTotalCount = new HashMap<>();
    private final Map<Status, TestStatistics> totalWeightedCount = new HashMap<>();
    private final Map<Status, TestStatistics> grandTotalWeightedCount = new HashMap<>();

    @Getter
    public static class TestStatistics {
        private final AtomicInteger total = new AtomicInteger();
        private final AtomicDouble percentage = new AtomicDouble();
    }
}
