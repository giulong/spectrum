package io.github.giulong.spectrum.utils.tests_comparators;

import static com.aventstack.extentreports.Status.*;

import java.util.HashMap;
import java.util.Map;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Test;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class StatusComparator implements TestsComparator {

    @JsonPropertyDescription("Weights of tests statuses. A lower weight means the test is shown before those with a higher one in the Extent report")
    private final Map<Status, Integer> weights = new HashMap<>() {{
        put(INFO, INFO.getLevel());
        put(PASS, PASS.getLevel());
        put(WARNING, WARNING.getLevel());
        put(SKIP, SKIP.getLevel());
        put(FAIL, FAIL.getLevel());
    }};

    @Override
    public int compare(final Test test1, final Test test2) {
        final Integer weight1 = weights.get(test1.getStatus());
        final Integer weight2 = weights.get(test2.getStatus());
        final int result = weight1.compareTo(weight2);

        log.trace("Comparing {} with {}: {}", weight1, weight2, result);
        return result;
    }
}
