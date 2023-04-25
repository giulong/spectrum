package com.giuliolongfils.agitation.internal;

import ch.qos.logback.classic.Level;
import com.aventstack.extentreports.ExtentTest;
import com.giuliolongfils.agitation.pojos.Configuration;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.lang.reflect.Method;
import java.util.Arrays;

import static com.aventstack.extentreports.markuputils.ExtentColor.YELLOW;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;

@Slf4j
@Builder
@Getter
public class EventsListener implements WebDriverListener {

    private ExtentTest extentTest;
    private Configuration.Events events;

    @Override
    public void beforeAnyWebElementCall(WebElement element, Method method, Object[] args) {
        log(events.getBeforeAnyWebElementCall(), String.format("About to call a method %s in element %s with parameters %s", method, element, Arrays.toString(args)));
    }

    @Override
    public void afterAnyWebElementCall(WebElement element, Method method, Object[] args, Object result) {
        log(events.getAfterAnyWebElementCall(), String.format("Method %s called in element %s with parameters %s returned %s", method, element, Arrays.toString(args), result));
    }

    protected void log(final Level level, final String msg) {
        switch (level.levelStr) {
            case "OFF" -> {
            }
            case "DEBUG" -> {
                log.debug(msg);

                if (log.isDebugEnabled()) {
                    extentTest.debug(msg);
                }
            }
            case "INFO" -> {
                log.info(msg);

                if (log.isInfoEnabled()) {
                    extentTest.info(msg);
                }
            }
            case "WARN" -> {
                log.warn(msg);

                if (log.isWarnEnabled()) {
                    extentTest.warning(createLabel(msg, YELLOW));
                }
            }
            default -> log.warn("Message '{}' won't be logged. Wrong log level set in configuration.yaml. Choose one among OFF, DEBUG, INFO, WARN", msg);
        }
    }
}
