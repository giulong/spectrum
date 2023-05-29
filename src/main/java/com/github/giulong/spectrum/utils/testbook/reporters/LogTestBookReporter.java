package com.github.giulong.spectrum.utils.testbook.reporters;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class LogTestBookReporter extends TestBookReporter {

    @SuppressWarnings("FieldMayBeFinal")
    private String template = "/testbook/template.txt";

    @Override
    public void doOutputFrom(final String interpolatedTemplate) {
        log.info("\n{}", interpolatedTemplate);
    }
}
