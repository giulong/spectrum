package io.github.giulong.spectrum.utils.tests_comparators;

import com.aventstack.extentreports.model.Test;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NameComparator implements TestsComparator {

    @Override
    public int compare(final Test test1, final Test test2) {
        final String name1 = test1.getName();
        final String name2 = test2.getName();
        final int result = name1.compareTo(name2);

        log.trace("Comparing {} with {}: {}", name1, name2, result);
        return result;
    }
}
