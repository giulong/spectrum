package io.github.giulong.spectrum.it_verifier.data;

import lombok.Getter;

import java.util.Map;

@Getter
@SuppressWarnings("unused")
public class Data {

    private TestBook testBook;
    private ExtentReport extentReport;
    private Summary summary;

    @Getter
    public static class TestBook {
        private String title;
        private Statistics statistics;
        private Qg qg;

        @Getter
        public static class Statistics {

            private Generic generic;
            private Group mappedWeighted;
            private Group grandTotalWeighted;
            private Group mapped;
            private Group grandTotal;

            @Getter
            public static class Generic {
                private String mappedTests;
                private String unmappedTests;
                private String totalWeighted;
            }

            @Getter
            public static class Group {
                private String successful;
                private String failed;
                private String aborted;
                private String disabled;
                private String notRun;
            }
        }

        @Getter
        public static class Qg {
            private String status;
            private String condition;
            private String evaluatedCondition;
        }
    }

    @Getter
    public static class ExtentReport {
        private Map<String, String> testLabels;
    }

    @Getter
    public static class Summary {
        private String title;
        private String successfulCount;
        private String successfulPercentage;
        private String failedCount;
        private String failedPercentage;
        private String abortedCount;
        private String abortedPercentage;
        private String disabledCount;
        private String disabledPercentage;
        private String condition;
        private String interpolatedCondition;
        private String conditionStatus;
    }
}
