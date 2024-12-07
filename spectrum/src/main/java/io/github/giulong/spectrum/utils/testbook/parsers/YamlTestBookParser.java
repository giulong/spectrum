package io.github.giulong.spectrum.utils.testbook.parsers;

import io.github.giulong.spectrum.pojos.testbook.TestBookTest;
import io.github.giulong.spectrum.pojos.testbook.TestBookYamlData;
import io.github.giulong.spectrum.utils.YamlUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class YamlTestBookParser extends TestBookParser {

    @Override
    public List<TestBookTest> parse() {
        log.debug("Reading lines of yaml testbook");

        return YamlUtils.getInstance()
                .readClient(path, TestBookYamlData.class)
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue()
                        .stream()
                        .map(v -> TestBookTest.builder()
                                .className(e.getKey())
                                .testName(v.getName())
                                .weight(v.getWeight())
                                .build()))
                .toList();
    }
}
