package com.giuliolongfils.spectrum.pojos;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class TestBook {
    private final Map<String, TestBookResult> tests = new HashMap<>();
    private final Map<String, TestBookResult> unmappedTests = new HashMap<>();
}
