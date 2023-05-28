package com.giuliolongfils.spectrum.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static freemarker.template.Configuration.VERSION_2_3_32;
import static java.util.Locale.US;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
@DisplayName("FreeMarkerConfiguration")
class FreeMarkerConfigurationTest {

    @Test
    @DisplayName("getInstance should return the singleton properly configured")
    public void getInstance() {
        final FreeMarkerConfiguration freeMarkerConfiguration = FreeMarkerConfiguration.getInstance();

        assertSame(FreeMarkerConfiguration.getInstance(), freeMarkerConfiguration);
        assertEquals(VERSION_2_3_32, freeMarkerConfiguration.getConfiguration().getIncompatibleImprovements());
        assertEquals(US, freeMarkerConfiguration.getConfiguration().getLocale());
        assertEquals("0.##;; roundingMode=halfUp", freeMarkerConfiguration.getConfiguration().getNumberFormat());
    }
}
