package io.github.giulong.spectrum.extensions.resolvers.bidi;

import lombok.Getter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.module.Network;

@Getter
public class NetworkResolver extends BiDiTypeBasedParameterResolver<Network> {

    public static final String NETWORK = "NETWORK";

    private final String key = NETWORK;

    private final Class<Network> type = Network.class;

    @Override
    public Network resolveParameterFor(final WebDriver driver) {
        return new Network(driver);
    }
}
