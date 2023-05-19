package com.giuliolongfils.spectrum.utils.testbook.parsers;

import com.giuliolongfils.spectrum.pojos.testbook.TestBookYamlData;
import com.giuliolongfils.spectrum.utils.YamlParser;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static com.giuliolongfils.spectrum.extensions.watchers.TestBookWatcher.SEPARATOR;

@Slf4j
public class YamlTestBookParser extends TestBookParser {

    @Override
    public List<String> parse() {
        return YamlParser.getInstance()
                .read(path, TestBookYamlData.class)
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue()
                        .stream()
                        .map(v -> String.format("%s%s%s", e.getKey(), SEPARATOR, v)))
                .collect(Collectors.toList());
    }
}
