package io.github.giulong.spectrum.utils.testbook.parsers;

import io.github.giulong.spectrum.pojos.testbook.TestBookTest;
import io.github.giulong.spectrum.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.toList;

@Slf4j
public abstract class SimpleTestBookParser extends TestBookParser {

    public abstract String getRegex();

    @Override
    public List<TestBookTest> parse() {
        log.debug("Reading lines of {} testbook", getClass().getSimpleName());
        final String regex = getRegex();
        final Pattern PATTERN = Pattern.compile(regex);

        return Arrays
                .stream(FileUtils.getInstance()
                        .read(String.format("/%s", path))
                        .split(lineSeparator()))
                .map(line -> {
                    Matcher matcher = PATTERN.matcher(line);
                    if (!matcher.matches()) {
                        throw new IllegalArgumentException(String.format("Line '%s' in TestBook doesn't match pattern %s", line, regex));
                    }

                    final String weight = matcher.group("weight");
                    return TestBookTest.builder()
                            .className(matcher.group("className"))
                            .testName(matcher.group("testName"))
                            .weight(Integer.parseInt(weight != null ? weight : "1"))
                            .build();
                }).collect(toList());
    }
}
