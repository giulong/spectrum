package io.github.giulong.spectrum.it_grid;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.openqa.selenium.grid.Main;

public class GridSessionListener implements LauncherSessionListener {

    @Override
    public void launcherSessionOpened(final LauncherSession session) {
        Main.main(new String[]{"standalone"});
    }
}
