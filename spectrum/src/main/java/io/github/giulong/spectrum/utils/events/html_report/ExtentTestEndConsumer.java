package io.github.giulong.spectrum.utils.events.html_report;

import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.annotation.JsonView;

import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.ExtentReporter;
import io.github.giulong.spectrum.utils.events.EventsConsumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@JsonView(Internal.class)
public class ExtentTestEndConsumer extends EventsConsumer {

    private final ExtentReporter extentReporter = ExtentReporter.getInstance();

    @Override
    public void accept(final Event event) {
        final Status status = event.getResult().getStatus();

        extentReporter.logTestEnd(event.getContext(), status);
        log.info("END execution of '{} -> {}': {}", event.getPrimaryId(), event.getSecondaryId(), status.name());
    }
}
