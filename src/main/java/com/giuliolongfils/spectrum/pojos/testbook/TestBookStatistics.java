package com.giuliolongfils.spectrum.pojos.testbook;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class TestBookStatistics {

    private final AtomicInteger grandTotal = new AtomicInteger();
    private final AtomicInteger successful = new AtomicInteger();
    private final AtomicInteger failed = new AtomicInteger();
    private final AtomicInteger aborted = new AtomicInteger();
    private final AtomicInteger disabled = new AtomicInteger();
    private final AtomicInteger notRun = new AtomicInteger();
    private final AtomicInteger grandTotalSuccessful = new AtomicInteger();
    private final AtomicInteger grandTotalFailed = new AtomicInteger();
    private final AtomicInteger grandTotalAborted = new AtomicInteger();
    private final AtomicInteger grandTotalDisabled = new AtomicInteger();
    private final AtomicInteger grandTotalNotRun = new AtomicInteger();
    private final Percentages percentages = new Percentages();

    @Getter
    @Setter
    public static class Percentages {
        private double tests;
        private double unmappedTests;
        private double successful;
        private double failed;
        private double aborted;
        private double disabled;
        private double notRun;
        private double grandTotalSuccessful;
        private double grandTotalFailed;
        private double grandTotalAborted;
        private double grandTotalDisabled;
        private double grandTotalNotRun;
    }
}
