package com.giuliolongfils.spectrum.utils;

import com.giuliolongfils.spectrum.utils.testbook.TxtTestBookParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("TxtTestBookParser")
class TxtTestBookParserTest {

    @InjectMocks
    private TxtTestBookParser testBookParser;

    @Test
    @DisplayName("parse should read the provided file line by line and return the list of test names")
    public void parse() {
        List<String> actual = testBookParser.parse();
        assertEquals(List.of("test 1", "another test"), actual);
    }

    @Test
    @DisplayName("buildTestBookFrom should build the testBook from the provided list of test names")
    public void buildTestBookFrom() {
        //testBookParser.setPath("src/test/resources/testbook.txt");
        //TestBook expected = new TestBook()
        //        .tests(Stream.of("test 1", "another test").collect(Collectors.toMap(Function.identity(), testName -> new TestBookResult())))
        //        .build();
//
        //TestBook actual = testBookParser.buildTestBook();
        //assertEquals(expected, actual);
    }
}