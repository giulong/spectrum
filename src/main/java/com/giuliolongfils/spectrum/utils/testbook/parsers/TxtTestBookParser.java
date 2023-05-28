package com.giuliolongfils.spectrum.utils.testbook.parsers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TxtTestBookParser extends SimpleTestBookParser {

    @Override
    public String getRegex() {
        return "^(?<className>[^:#]+?)::(?<testName>[^:#]+?)(##(?<weight>\\d+))?$";
    }
}
