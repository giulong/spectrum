package com.github.giulong.spectrum.utils.testbook.parsers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvTestBookParser extends SimpleTestBookParser {

    @Override
    public String getRegex() {
        return "^(?<className>[^,]+),(?<testName>[^,]+)(,(?<weight>\\d+))?$";
    }
}
