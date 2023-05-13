package com.giuliolongfils.spectrum.utils.testbook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.giuliolongfils.spectrum.pojos.testbook.TestBookResult;
import com.giuliolongfils.spectrum.utils.testbook.parsers.TestBookParser;
import com.giuliolongfils.spectrum.utils.testbook.reporters.TestBookReporter;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class TestBook {

    private TestBookParser parser;

    private List<TestBookReporter> reporters;

    @JsonIgnore
    private final Map<String, TestBookResult> tests = new HashMap<>();

    @JsonIgnore
    private final Map<String, TestBookResult> unmappedTests = new HashMap<>();
}
