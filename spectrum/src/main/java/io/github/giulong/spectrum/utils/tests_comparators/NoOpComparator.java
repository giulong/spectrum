package io.github.giulong.spectrum.utils.tests_comparators;

import com.aventstack.extentreports.model.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpComparator implements TestsComparator {

    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    @Override
    public int compare(final Test test1, final Test test2) {
        log.trace("Returning 0");
        return 0;
    }
}
