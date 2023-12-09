package io.github.giulong.spectrum.it_grid;

import lombok.extern.slf4j.Slf4j;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.openqa.selenium.grid.Main;

@Slf4j
public class GridSessionListener implements LauncherSessionListener {

    @Override
    public void launcherSessionOpened(final LauncherSession session) {
        Main.main(new String[]{"standalone"});
    }
}
