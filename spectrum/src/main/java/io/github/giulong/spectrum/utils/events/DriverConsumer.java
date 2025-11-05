package io.github.giulong.spectrum.utils.events;

import static io.github.giulong.spectrum.enums.Result.DISABLED;
import static io.github.giulong.spectrum.extensions.resolvers.bidi.BrowsingContextInspectorResolver.BROWSING_CONTEXT_INSPECTOR;
import static io.github.giulong.spectrum.extensions.resolvers.bidi.LogInspectorResolver.LOG_INSPECTOR;
import static io.github.giulong.spectrum.extensions.resolvers.bidi.NetworkResolver.NETWORK;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonView;

import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Configuration;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.bidi.module.BrowsingContextInspector;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.bidi.module.Network;

@Slf4j
@JsonView(Internal.class)
public class DriverConsumer extends EventsConsumer {

    private final Configuration configuration = Configuration.getInstance();

    @Override
    protected boolean shouldAccept(final Event event) {
        return !DISABLED.equals(event.getResult());
    }

    @Override
    public void accept(final Event event) {
        final Configuration.Runtime runtime = configuration.getRuntime();

        if (!configuration.getDrivers().isKeepOpen()) {
            runtime.getDriver().shutdown();
        }

        runtime.getEnvironment().shutdown();

        final ExtensionContext.Store store = event.getContext().getStore(GLOBAL);
        Optional.ofNullable(store.get(BROWSING_CONTEXT_INSPECTOR, BrowsingContextInspector.class)).ifPresent(BrowsingContextInspector::close);
        Optional.ofNullable(store.get(LOG_INSPECTOR, LogInspector.class)).ifPresent(LogInspector::close);
        Optional.ofNullable(store.get(NETWORK, Network.class)).ifPresent(Network::close);
    }
}
