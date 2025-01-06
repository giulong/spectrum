package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebDriverEventTest {

    @Mock
    private WebElement webElement1;

    @Mock
    private Object object;

    @Mock
    private WebElement webElement2;

    @InjectMocks
    private WebDriverEvent webDriverEvent;

    @Test
    @DisplayName("removeTagsFromMessage should remove all the tags from the message")
    void removeTagsFromMessage() {
        final String message = "<tag> mess</tag>age <another>";
        Reflections.setField("message", webDriverEvent, message);

        assertEquals(" message ", webDriverEvent.removeTagsFromMessage());
    }

    @Test
    @DisplayName("findWebElementsInArgs should return a list of all the web elements found in args")
    void findWebElementsInArgs() {
        Reflections.setField("args", webDriverEvent, List.of(webElement1, object, webElement2));

        assertEquals(List.of(webElement1, webElement2), webDriverEvent.findWebElementsInArgs());
    }
}
